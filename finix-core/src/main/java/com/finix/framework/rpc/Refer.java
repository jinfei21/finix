package com.finix.framework.rpc;

public interface Refer extends Caller{
	
    String getInterface();
    
	boolean isAvailable();
	
	URL getReferUrl();
	
	URL getServiceUrl();
	
	void init();
	
	void destroy();
}
