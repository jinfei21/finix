package com.finix.framework.rpc;

import java.lang.reflect.Method;

public interface Provider<T> extends Caller{

	Method lookupMethod(String methodName,String[] parameterTypes);

	Class<T> getInterface();
	
	T getImpl();
	
	URL getServiceUrl();
	
	void setServiceUrl(URL serviceUrl);
	
	void destroy();
	
	void init();
}
