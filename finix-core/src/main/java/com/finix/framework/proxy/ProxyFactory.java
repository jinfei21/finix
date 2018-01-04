package com.finix.framework.proxy;

import java.lang.reflect.InvocationHandler;

public interface ProxyFactory {
    <T> T getProxy(Class<T> clazz, InvocationHandler invocationHandler);
}
