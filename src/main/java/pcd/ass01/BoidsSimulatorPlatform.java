package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BoidsSimulatorPlatform extends AbstractBoidsSimulator implements BoidsSimulator{
    private BoidsModel model;

    private List<Thread> workers = new ArrayList<>();

    public BoidsSimulatorPlatform(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
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
        boolean starting = true;
        while (LOOP) {
            if (model.isRunning()) {
                var t0 = System.currentTimeMillis();
                if(starting){
                    this.initWorkers(model);
                    workers.forEach(Thread::start);
                    starting = false;
                }
                updateView(t0);
            } else if(!starting) {
                this.workers.forEach(t -> {
                    try {
                        t.join();
                    } catch (InterruptedException ignore) {}
                });
                this.workers.clear();
                starting = true;
            }
        }
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
