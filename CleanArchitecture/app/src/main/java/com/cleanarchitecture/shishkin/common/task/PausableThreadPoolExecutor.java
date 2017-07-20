package com.cleanarchitecture.shishkin.common.task;

import com.cleanarchitecture.shishkin.common.state.IViewStateListener;
import com.cleanarchitecture.shishkin.common.state.ViewStateObserver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PausableThreadPoolExecutor extends ThreadPoolExecutor implements IViewStateListener {
    private boolean isPaused;
    private ReentrantLock mLock;
    private Condition mCondition;
    private ViewStateObserver mLifecycle;

    /**
     * @param corePoolSize    The size of the pool
     * @param maximumPoolSize The maximum size of the pool
     * @param keepAliveTime   The amount of time you wish to keep a single task alive
     * @param unit            The unit of time that the keep alive time represents
     * @param workQueue       The queue that holds your tasks
     * @see {@link ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue)}
     */
    public PausableThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        mLock = new ReentrantLock();
        mCondition = mLock.newCondition();
        mLifecycle = new ViewStateObserver(this);
    }

    /**
     * @param thread   The thread being executed
     * @param runnable The runnable task
     * @see {@link ThreadPoolExecutor#beforeExecute(Thread, Runnable)}
     */
    @Override
    protected void beforeExecute(final Thread thread, final Runnable runnable) {
        super.beforeExecute(thread, runnable);
        mLock.lock();
        try {
            while (isPaused) mCondition.await();
        } catch (InterruptedException ie) {
            thread.interrupt();
        } finally {
            mLock.unlock();
        }
    }

    public boolean isRunning() {
        return !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Pause the execution
     */
    private void pause() {
        mLock.lock();
        try {
            isPaused = true;
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Resume pool execution
     */
    private void resume() {
        mLock.lock();
        try {
            isPaused = false;
            mCondition.signalAll();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void setState(final int state) {
        mLifecycle.setState(state);
    }

    @Override
    public int getState() {
        return mLifecycle.getState();
    }

    @Override
    public void onCreateState() {
    }

    @Override
    public void onReadyState() {
    }

    @Override
    public void onResumeState() {
        resume();
    }

    @Override
    public void onPauseState() {
        pause();
    }

    @Override
    public void onDestroyState() {
        if (!isShutdown()) {
            shutdown();
        }
    }
}