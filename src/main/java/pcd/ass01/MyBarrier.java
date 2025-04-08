package pcd.ass01;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyBarrier {
    private final Lock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private final Runnable runnable;
    private boolean broken = false;
    private final int parties;
    private int counter = 0;

    public MyBarrier(int parties) {
        this(parties, null);
    }

    public MyBarrier(int parties, Runnable runnable) {
        this.parties = parties;
        this.runnable = runnable;
    }

    public void await() throws InterruptedException, BrokenBarrierException {
        lock.lock();
        try {
            if (broken) {
                throw new BrokenBarrierException();
            }
            counter++;
            if (counter == parties) {
                breakBarrier();
            } else {
                cond.await();
            }
        } finally {
            lock.unlock();
        }
    }

    private void breakBarrier() {
        broken = true;
        cond.signalAll();
        if (runnable != null) {
            runnable.run();
        }
    }

    public void reset() {
        lock.lock();
        if (broken) {
            broken = false;
            counter = 0;
        }
        lock.unlock();
    }
}
