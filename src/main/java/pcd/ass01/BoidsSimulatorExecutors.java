package pcd.ass01;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BoidsSimulatorExecutors extends AbstractBoidsSimulator implements BoidsSimulator {

    private final ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private boolean toStart = false;

    protected BoidsSimulatorExecutors(BoidsModel model) {
        super(model);
    }

    @Override
    public void runSimulation() {
        toStart = true;
        while (true) {
            if (model.isRunning()) {
                if (toStart) {
                    start();
                }
                var t0 = System.currentTimeMillis();
                updateBoids();
                updateView(t0);
            } else if (!toStart) {
                stop();
            }
        }
    }

    private void updateBoids() {
        model.getBoids().stream()
                .map(boid -> exec.submit(() -> boid.updateVelocity(model)))
                .forEach(this::waitForActionDone);

        model.getBoids().stream()
                .map(boid -> exec.submit(() -> boid.updatePos(model)))
                .forEach(this::waitForActionDone);
    }

    private void stop() {
        model.clearBoids();
        view.ifPresent(BoidsView::enableStartStopButton);
        toStart = true;
    }

    private void start() {
        model.generateBoids();
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
