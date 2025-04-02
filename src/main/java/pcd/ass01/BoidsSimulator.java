package pcd.ass01;

public interface BoidsSimulator {

    static BoidsSimulator getSimulator(BoidsModel model, SimulatorType type) {
        return switch (type) {
            case SEQUENTIAL -> new BoidsSimulatorSequential(model);
            case PLATFORM -> new BoidsSimulatorPlatform(model);
            case EXECUTORS -> new BoidsSimulatorExecutors(model);
            case VIRTUAL -> null;
        };
    }
    void attachView(BoidsView view);

    void runSimulation();
}
