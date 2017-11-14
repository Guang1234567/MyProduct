package com.example.myproduct.lib.common.utils.rx.eventbus;

/**
 * An {@link RuntimeException} thrown in cases something went wrong inside RxEventBus.
 *
 * @author lihanguang
 * @date 2017/5/15 13:47:15
 */
public class RxEventBusException extends RuntimeException {

    private static final long serialVersionUID = 4261643057901261017L;

    public RxEventBusException(String detailMessage) {
        super(detailMessage);
    }

    public RxEventBusException(Throwable throwable) {
        super(throwable);
    }

    public RxEventBusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
