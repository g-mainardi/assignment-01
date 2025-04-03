package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static pcd.ass01.Partitioner.partition;

public class BoidsSimulatorPlatform extends AbstractBoidsSimulator implements BoidsSimulator{
    private final List<Thread> workers = new ArrayList<>();
    private boolean toStart = false;
    private boolean toResume = false;

    public BoidsSimulatorPlatform(BoidsModel model) {
        super(model);
    }

    private void initWorkers(BoidsModel model) {
        var boids = model.getBoids();
        int nThreads = Runtime.getRuntime().availableProcessors();

        List<List<Boid>> partitions = partition(boids, nThreads);
        CyclicBarrier velBarrier = new CyclicBarrier(nThreads);
        CyclicBarrier posBarrier = new CyclicBarrier(nThreads);

        for (List<Boid> partition : partitions) {
            workers.add(new Thread(() -> update(partition, velBarrier, posBarrier)));
        }
    }

    private void update(List<Boid> boids, CyclicBarrier velBarrier, CyclicBarrier posBarrier) {
        while (model.isRunning()) {
            if (model.isSuspended()) {
                continue;
            }
            boids.forEach(boid -> boid.updateVelocity(model));
            try {
                velBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("Thread [" + Thread.currentThread().getName() + "] interrupted while waiting for velocity barrier");
            }
            velBarrier.reset();
            boids.forEach(boid -> boid.updatePos(model));
            try {
                posBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("Thread [" + Thread.currentThread().getName() + "] interrupted while waiting for position barrier");
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
                var t0 = System.currentTimeMillis();
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
                updateView(t0);
            } else if(!toStart) {
                stop();
            }
        }
    }

    private void suspend() {
        this.toResume = true;
        this.view.ifPresent(BoidsView::enableSuspendResumeButton);
    }

    private void resume() {
        this.toResume = false;
        this.view.ifPresent(BoidsView::enableSuspendResumeButton);
    }

    private void start() {
        this.model.generateBoids();
        this.initWorkers(model);
        this.workers.forEach(Thread::start);
        this.toStart = false;
        this.view.ifPresent(BoidsView::enableStartStopButton);
    }

    private void stop() {
        this.workers.forEach(Thread::interrupt);
        this.workers.clear();
        this.model.clearBoids();
        this.toStart = true;
        if (model.isSuspended()){
            this.toResume = false;
            this.view.ifPresent(BoidsView::resumeAction);
        }
        this.view.ifPresent(v -> v.update(framerate));
        this.view.ifPresent(BoidsView::enableStartStopButton);
    }

}
