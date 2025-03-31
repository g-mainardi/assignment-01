package pcd.ass01;

public interface BoidsSimulator {

    static BoidsSimulator getSimulator(BoidsModel model, SimulatorType type) {
        return switch (type) {
            case SEQUENTIAL -> new BoidsSimulatorSequential(model);
            case PLATFORM -> new BoidsSimulatorPlatform(model);
            case EXECUTORS -> null;
            case VIRTUAL -> null;
        };
    }
    void attachView(BoidsView view);
    void updateModel(BoidsModel model);

    void runSimulation();
    void resumeSimulation();
    void stopSimulation();
}
