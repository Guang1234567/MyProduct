package com.example.myproduct.lib.common.utils.rx.eventbus;

/**
 * This Event is posted by RxEventBus when an exception occurs inside a subscriber's event handling method.
 *
 * @author lihanguang
 * @date 2017/5/15 13:47:15
 */
public final class RxEventBusSubscriberException {
    /**
     * The {@link RxEventBus} instance to with the original event was posted to.
     */
    public final RxEventBus eventBus;

    /**
     * The Throwable thrown by a subscriber.
     */
    public final Throwable throwable;

    /**
     * The original event that could not be delivered to any subscriber.
     */
    public final Object causingEvent;

    /**
     * The subscriber that threw the Throwable.
     */
    public final Object causingSubscriber;

    public RxEventBusSubscriberException(RxEventBus eventBus, Throwable throwable, Object causingEvent,
                                         Object causingSubscriber) {
        this.eventBus = eventBus;
        this.throwable = throwable;
        this.causingEvent = causingEvent;
        this.causingSubscriber = causingSubscriber;
    }

}
