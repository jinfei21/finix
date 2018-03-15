package com.finix.gateway.common;

@FunctionalInterface
public interface StateChangeListener<S> {
    void onStateChange(S oldState, S newState, Object event);
}
