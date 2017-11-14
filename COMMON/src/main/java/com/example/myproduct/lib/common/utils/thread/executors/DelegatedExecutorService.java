package com.example.myproduct.lib.common.utils.thread.executors;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 摘抄 {@link Executors.DelegatedExecutorService}
 *
 * @author lihanguang
 * @date 2017/10/13 10:17
 */

public class DelegatedExecutorService<D extends ExecutorService> implements ExecutorService {
    private final D mE;

    public DelegatedExecutorService(D executor) {
        mE = executor;
    }

    protected D getDelegator() {
        return mE;
    }

    @Override
    public void execute(Runnable command) {
        mE.execute(command);
    }

    @Override
    public void shutdown() {
        mE.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return mE.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return mE.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return mE.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return mE.awaitTermination(timeout, unit);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return mE.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return mE.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return mE.submit(task, result);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException {
        return mE.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                         long timeout, TimeUnit unit)
            throws InterruptedException {
        return mE.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return mE.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                           long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return mE.invokeAny(tasks, timeout, unit);
    }
}
