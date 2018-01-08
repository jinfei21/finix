package com.finix.framework.rpc;

public interface Protocol {

	String getName();
	
	 Exporter export(Provider provider,URL serviceUrl);
	
	 Refer refer(String interfaceClass,URL referUrl,URL serviceUrl);
	
	void destroy();
}
