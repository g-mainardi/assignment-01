package pcd.ass01;

import java.util.Optional;

public abstract class AbstractBoidsSimulator implements BoidsSimulator {
    protected BoidsModel model;
    protected Optional<BoidsView> view;
    protected boolean toStart = false;
    protected boolean toResume = false;
    private long startingTime;
    private int iterations = 0;
    private boolean iterationsPrinted = false;

    private static final int FRAMERATE = 50;
    protected int framerate;

    private long t0;

    protected AbstractBoidsSimulator(BoidsModel model) {
        this.model = model;
        this.view = Optional.empty();
    }

    @Override
    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    protected void updateView() {
        iterations++;
        if (iterations == 100 && !iterationsPrinted) {
            System.out.println("100 ITERATIONS IN SECONDS: " + (System.currentTimeMillis() - startingTime));
            iterationsPrinted = true;
        }
        if (view.isPresent()) {
            view.get().update();
            view.get().updateFrameRate(framerate);
            var t1 = System.currentTimeMillis();
            var dtElapsed = t1 - t0;
            var frameratePeriod = 1000 / FRAMERATE;

            t0 = System.currentTimeMillis();
            if (dtElapsed < frameratePeriod) {
                try {
                    Thread.sleep(frameratePeriod - dtElapsed);
                } catch (Exception ignore) {}
                framerate = FRAMERATE;
            } else {
                framerate = (int) (1000 / dtElapsed);
            }
        }
    }

    protected void suspend() {
        this.toResume = true;
        this.view.ifPresent(BoidsView::enableSuspendResumeButton);
    }

    protected void resume() {
        this.toResume = false;
        this.view.ifPresent(BoidsView::enableSuspendResumeButton);
    }

    protected void start() {
        this.model.generateBoids();
        init();
        this.startingTime = System.currentTimeMillis();
        this.t0 = System.currentTimeMillis();
        this.toStart = false;
        this.view.ifPresent(BoidsView::enableStartStopButton);
    }

    protected void stop() {
        clear();
        this.model.clearBoids();
        this.toStart = true;
        this.iterationsPrinted = false;
        this.iterations = 0;
        if (model.isSuspended()){
            this.toResume = false;
            this.view.ifPresent(BoidsView::resumeAction);
        }
        this.view.ifPresent(v -> {
            v.update();
            v.updateFrameRate(0);
            v.enableStartStopButton();
        });
    }

    protected abstract void clear();

    protected abstract void init();

}
