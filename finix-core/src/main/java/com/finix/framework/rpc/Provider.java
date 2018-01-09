package com.finix.framework.rpc;

import java.lang.reflect.Method;

public interface Provider extends Caller{

	Method lookupMethod(String methodName,String paramDesc);

	String getInterface();
	
	URL getServiceUrl();
	
	void setServiceUrl(URL serviceUrl);
	
	void destroy();
	
	void init();
}
