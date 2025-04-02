package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BoidsSimulatorPlatform extends AbstractBoidsSimulator implements BoidsSimulator{
    private final List<Thread> workers = new ArrayList<>();
    private boolean toStart = false;

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
            boids.forEach(boid -> boid.updateVelocity(model));
            try {
                velBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println(e.getMessage());
            }
            velBarrier.reset();
            boids.forEach(boid -> boid.updatePos(model));
            try {
                posBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println(e.getMessage());
            }
            posBarrier.reset();
        }
    }

    @Override
    public void runSimulation() {
        this.toStart = true;
        while (true) {
            if (model.isRunning()) {
                var t0 = System.currentTimeMillis();
                if(toStart){
                    start();
                }
                updateView(t0);
            } else if(!toStart) {
                stop();
            }
        }
    }

    private void start() {
        this.model.generateBoids();
        this.initWorkers(model);
        this.workers.forEach(Thread::start);
        this.toStart = false;
        this.view.ifPresent(BoidsView::enableStartStopButton);
    }

    private void stop() {
        this.workers.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException ignore) {}
        });
        this.workers.clear();
        this.model.clearBoids();
        this.toStart = true;
        this.view.ifPresent(BoidsView::enableStartStopButton);
    }

    private void startWorkersAndWait() {
        workers.forEach(Thread::start);
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for worker to finish");
            }
        }
    }

    private static <E> List<List<E>> partition(List<E> elems, int numberOfPartitions) {
        List<List<E>> partitions = new ArrayList<>();
        for (int i = 0; i < numberOfPartitions; i++) {
            partitions.add(new ArrayList<E>());
        }
        for (int i = 0; i < elems.size(); i++) {
            partitions.get(i % numberOfPartitions).add(elems.get(i));
        }
        return partitions;
    }
}
