package pcd.ass01;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBoidsSimulator implements BoidsSimulator {
    protected BoidsModel model;
    protected Optional<BoidsView> view;
    protected boolean toStart = false;
    protected boolean toResume = false;

    public static final int FRAMERATE_UPDATE_FREQUENCY = 1000;
    private static final int FRAMERATE = 50;
    protected int framerate;

    private int updateCounter = 0;
    private final Lock updateCounterLock = new ReentrantLock();

    protected AbstractBoidsSimulator(BoidsModel model) {
        this.model = model;
        this.view = Optional.empty();
    }

    public int getUpdateCounter() {
        return updateCounter;
    }

    public void resetUpdateCounter() {
        updateCounterLock.lock();
        this.updateCounter = 0;
        updateCounterLock.unlock();
    }

    public void incUpdateCounter() {
        updateCounterLock.lock();
        this.updateCounter++;
        updateCounterLock.unlock();
    }

    @Override
    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    protected void updateView(final long t0) {
        if (view.isPresent()) {
            view.get().update();
            view.get().updateFrameRate(framerate);
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

    protected void start() {
        this.model.generateBoids();
        resetUpdateCounter();
        init();
        new Thread(this::timer).start();
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
        this.view.ifPresent(v -> {
            v.update();
            v.updateFrameRate(0);
            v.enableStartStopButton();
        });
    }

    private int calcFrameRate(double updates) {
        return (int) (updates / ((double) FRAMERATE_UPDATE_FREQUENCY / 1000L));
    }

    private void timer() {
        while (model.isRunning()) {
            if (model.isSuspended()) {
                continue;
            }
            try {
                Thread.sleep(FRAMERATE_UPDATE_FREQUENCY);
                framerate = calcFrameRate(getUpdateCounter());
                resetUpdateCounter();
                this.view.ifPresent(v -> v.updateFrameRate(framerate));
            } catch (InterruptedException e) {
                System.out.println("tirem inrettuprer");
            }
        }
    }

    protected abstract void clear();

    protected abstract void init();

}
