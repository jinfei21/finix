package com.finix.framework.rpc;

public interface Refer extends Caller{
	
	boolean isAvailable();
	
	URL getReferUrl();
	
	URL getServiceUrl();
	
	void init();
	
	void destroy();
}
