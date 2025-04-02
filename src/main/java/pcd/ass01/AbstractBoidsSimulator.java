package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public abstract class AbstractBoidsSimulator implements BoidsSimulator {
    protected BoidsModel model;
    protected Optional<BoidsView> view;

    private static final int FRAMERATE = 50;
    private int framerate;

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

}
