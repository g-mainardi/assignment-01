package pcd.ass01;

public class BoidsSimulatorSequential extends AbstractBoidsSimulator implements BoidsSimulator{

    public BoidsSimulatorSequential(BoidsModel model) {
        super(model);
    }

    public void runSimulation() {
        while (true) {
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
