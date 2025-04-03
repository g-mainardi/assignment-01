package pcd.ass01;

import java.util.Optional;

public abstract class AbstractBoidsSimulator implements BoidsSimulator {
    protected BoidsModel model;
    protected Optional<BoidsView> view;
    protected boolean toStart = false;
    protected boolean toResume = false;

    private static final int FRAMERATE = 50;
    protected int framerate;

    protected AbstractBoidsSimulator(BoidsModel model) {
        this.model = model;
        this.view = Optional.empty();
    }

    @Override
    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    protected void updateView(final long t0) {
        if (view.isPresent()) {
            view.get().update(framerate);
            var t1 = System.currentTimeMillis();
            var dtElapsed = t1 - t0;
            var frameratePeriod = 1000 / FRAMERATE;

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

    protected void  start() {
        this.model.generateBoids();
        init();
        this.toStart = false;
        this.view.ifPresent(BoidsView::enableStartStopButton);
    }

    protected void stop() {
        clear();
        this.model.clearBoids();
        this.toStart = true;
        if (model.isSuspended()){
            this.toResume = false;
            this.view.ifPresent(BoidsView::resumeAction);
        }
        this.view.ifPresent(v -> v.update(framerate));
        this.view.ifPresent(BoidsView::enableStartStopButton);

    }

    protected abstract void clear();

    protected abstract void init();

}
