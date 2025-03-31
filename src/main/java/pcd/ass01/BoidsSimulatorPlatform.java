package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BoidsSimulatorPlatform implements BoidsSimulator {
    private BoidsModel model;
    private Optional<BoidsView> view;

    private static final int FRAMERATE = 50;
    private int framerate;
    private List<Thread> workers = new ArrayList<>();
    private boolean interrupted = false;

    public BoidsSimulatorPlatform(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
        initWorkers(model);
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
        while (!interrupted) {
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
    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    @Override
    public void updateModel(BoidsModel model) {
        this.model = model;
    }

    @Override
    public void runSimulation() {
        workers.forEach(Thread::start);
        while (true) {
            if (!interrupted) {
                var t0 = System.currentTimeMillis();
                if (view.isPresent()) {
                    view.get().update(framerate);
                    var t1 = System.currentTimeMillis();
                    var dtElapsed = t1 - t0;
                    var framratePeriod = 1000 / FRAMERATE;

                    if (dtElapsed < framratePeriod) {
                        try {
                            Thread.sleep(framratePeriod - dtElapsed);
                        } catch (Exception ex) {
                        }
                        framerate = FRAMERATE;
                    } else {
                        framerate = (int) (1000 / dtElapsed);
                    }
                }
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

    @Override
    public void resumeSimulation() {
        interrupted = false;
    }

    @Override
    public void stopSimulation() {
        interrupted = true;
    }
}
