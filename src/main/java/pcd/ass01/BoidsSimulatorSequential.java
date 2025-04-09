package pcd.ass01;

public class BoidsSimulatorSequential extends AbstractBoidsSimulator implements BoidsSimulator{

    public BoidsSimulatorSequential(BoidsModel model) {
        super(model);
    }

    @Override
    protected void clear() {
        
    }

    @Override
    protected void init() {

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
                updateView();
            } else if (!toStart) {
                stop();
            }
        }
    }

    private void updateBoids() {
        var boids = model.getBoids();

        for (Boid boid : boids) {
            boid.updateVelocity(model);
        }

        for (Boid boid : boids) {
            boid.updatePos(model);
        }
    }
}
