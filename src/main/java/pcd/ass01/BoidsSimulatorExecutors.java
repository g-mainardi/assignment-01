package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static pcd.ass01.ListUtils.partitionBySize;

public class BoidsSimulatorExecutors extends AbstractBoidsSimulator implements BoidsSimulator {

    public static final int MAX_BOIDS_PER_TASK = 15;
    private List<List<Boid>> batches = new ArrayList<>();
    private final ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    protected BoidsSimulatorExecutors(BoidsModel model) {
        super(model);
    }

    private void initBatches() {
        batches = partitionBySize(model.getBoids(), MAX_BOIDS_PER_TASK);
    }

    private void clearBatches() {
        batches = new ArrayList<>();
    }

    @Override
    public void runSimulation() {
        this.toStart = true;
        this.toResume = false;
        while (true) {
            if (model.isRunning()) {
                if (toStart) {
                    start();
                }
                if (model.isSuspended()) {
                    if(!toResume) {
                        suspend();
                    }
                } else {
                    if (toResume) {
                        resume();
                    }
                    updateBoids();
                }
            } else if (!toStart) {
                stop();
            }
        }
    }

    private void updateBoids() {
        batches.stream()
                .map(batch -> exec.submit(() -> batch.forEach(boid -> boid.updateVelocity(model))))
                .forEach(this::waitForActionDone);
        batches.stream()
                .map(batch -> exec.submit(() -> batch.forEach(boid -> boid.updatePos(model))))
                .forEach(this::waitForActionDone);
        this.updateView();
    }

    protected void clear() {
        this.clearBatches();
    }

    protected void init() {
        this.initBatches();
    }

    private <A extends Future<?>> void waitForActionDone(A action) {
        try {
            action.get();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted while waiting for task: " + e);
        } catch (ExecutionException e) {
            System.out.println("Task execution threw an exception: " + e);
        }
    }
}
