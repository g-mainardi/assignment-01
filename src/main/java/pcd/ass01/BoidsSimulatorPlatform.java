package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

import static pcd.ass01.ListUtils.partitionByNumber;

public class BoidsSimulatorPlatform extends AbstractBoidsSimulator implements BoidsSimulator {
    private static final int THREADS_NUMBER = Runtime.getRuntime().availableProcessors();
    private final List<Thread> workers = new ArrayList<>();

    public BoidsSimulatorPlatform(BoidsModel model) {
        super(model);
    }

    private void initWorkers(BoidsModel model) {
        var boids = model.getBoids();

        List<List<Boid>> partitions = partitionByNumber(boids, THREADS_NUMBER);
        MyBarrier velBarrier = new MyBarrier(THREADS_NUMBER);
        MyBarrier posBarrier = new MyBarrier(THREADS_NUMBER, this::updateView);

        for (List<Boid> partition : partitions) {
            workers.add(new Thread(() -> update(partition, velBarrier, posBarrier)));
        }
    }

    protected void update(List<Boid> boids, MyBarrier velBarrier, MyBarrier posBarrier) {
        while (model.isRunning()) {
            if (model.isSuspended()) {
                continue;
            }
            boids.forEach(boid -> boid.updateVelocity(model));
            try {
                velBarrier.await();
            } catch (InterruptedException e) {
                System.out.println("Thread [" + Thread.currentThread().getName() + "] interrupted while waiting for velocity barrier");
            } catch (BrokenBarrierException e) {
                System.out.println("Thread [" + Thread.currentThread().getName() + "] encountered broken velocity barrier");
            }
            velBarrier.reset();
            boids.forEach(boid -> boid.updatePos(model));
            try {
                posBarrier.await();
            } catch (InterruptedException e) {
                System.out.println("Thread [" + Thread.currentThread().getName() + "] interrupted while waiting for position barrier");
            } catch (BrokenBarrierException e) {
                System.out.println("Thread [" + Thread.currentThread().getName() + "] encountered broken position barrier");
            }
            posBarrier.reset();
        }
    }

    @Override
    public void runSimulation() {
        this.toStart = true;
        this.toResume = false;
        while (true) {
            if (model.isRunning()) {
                if(toStart){
                    start();
                }
                if (model.isSuspended()) {
                    if (!toResume) {
                        suspend();
                    }
                } else if (toResume) {
                    resume();
                }
            } else if(!toStart) {
                stop();
            }
        }
    }

    protected void init() {
        this.initWorkers(this.model);
        this.workers.forEach(Thread::start);
    }

    protected void clear() {
        this.workers.forEach(Thread::interrupt);
        this.workers.clear();
    }

}
