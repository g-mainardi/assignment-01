package pcd.ass01;

import java.util.Optional;

public class BoidsSimulatorSequential extends AbstractBoidsSimulator implements BoidsSimulator{

    private BoidsModel model;

    public BoidsSimulatorSequential(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
    }

    public void runSimulation() {
        while (LOOP) {
            if (model.isRunning()) {
                var t0 = System.currentTimeMillis();
                var boids = model.getBoids();
    		/*
    		for (Boid boid : boids) {
                boid.update(model);
            }
            */

                /*
                 * Improved correctness: first update velocities...
                 */
                for (Boid boid : boids) {
                    boid.updateVelocity(model);
                }

                /*
                 * ..then update positions
                 */
                for (Boid boid : boids) {
                    boid.updatePos(model);
                }
                updateView(t0);
            }
        }
    }
}
