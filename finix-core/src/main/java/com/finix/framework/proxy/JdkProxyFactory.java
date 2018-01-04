package com.finix.framework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JdkProxyFactory implements ProxyFactory {

	@Override
	public <T> T getProxy(Class<T> clazz, InvocationHandler invocationHandler) {
		return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, invocationHandler);
	}

}
