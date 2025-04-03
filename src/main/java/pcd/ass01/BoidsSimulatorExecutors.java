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
    private boolean toStart = false;

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
                var t0 = System.currentTimeMillis();
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
                updateView(t0);
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
    }

    private void stop() {
        model.clearBoids();
        this.clearBatches();
        view.ifPresent(BoidsView::enableStartStopButton);
        toStart = true;
    }

    private void start() {
        model.generateBoids();
        this.initBatches();
        view.ifPresent(BoidsView::enableStartStopButton);
        toStart = false;
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
