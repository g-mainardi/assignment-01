package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import static pcd.ass01.ListUtils.partitionBySize;

public class BoidsSimulatorVirtual extends BoidsSimulatorPlatform {

    public static final int MAX_BOIDS_PER_VIRTUAL = 1;
    private List<List<Boid>> batches = new ArrayList<>();

    protected BoidsSimulatorVirtual(BoidsModel model) {
        super(model);
    }

    private void initBatches() {
        batches = partitionBySize(model.getBoids(), MAX_BOIDS_PER_VIRTUAL);
    }

    private void clearBatches() {
        batches = new ArrayList<>();
    }

    protected void clear() {
        this.clearBatches();
    }

    protected void init() {
        this.initBatches();
        this.initVirtualThreads();
    }

    private void initVirtualThreads() {
        CyclicBarrier velBarrier = new CyclicBarrier(batches.size());
        CyclicBarrier posBarrier = new CyclicBarrier(batches.size());

        batches.forEach(batch -> Thread.ofVirtual().start(() -> update(batch, velBarrier, posBarrier)));
    }
}
