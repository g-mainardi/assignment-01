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
    private final boolean runnableToWait;

    public MyBarrier(int parties) {
        this(parties, null, false);
    }

    public MyBarrier(int parties, Runnable runnable) {
        this(parties, runnable, false);
    }

    public MyBarrier(int parties, Runnable runnable, boolean runnableToWait) {
        this.parties = parties;
        this.runnable = runnable;
        this.runnableToWait = runnableToWait;
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
            var toExecute = new Thread(runnable);
            toExecute.start();
            if (runnableToWait) {
                try {
                    toExecute.join();
                } catch (InterruptedException e) {
                    System.out.println("Thread [" + Thread.currentThread().getName() + "] interrupted execution for barrier break");
                }
            }
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
