package com.example.myproduct.lib.common.utils.thread.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 摘抄 {@link Executors.FinalizableDelegatedExecutorService}
 *
 * @author lihanguang
 * @date 2017/10/13 10:46
 */

class FinalizableDelegatedExecutorService<D extends ExecutorService> extends DelegatedExecutorService<D> {
    FinalizableDelegatedExecutorService(D executor) {
        super(executor);
    }

    protected void finalize() {
        super.shutdown();
    }
}
