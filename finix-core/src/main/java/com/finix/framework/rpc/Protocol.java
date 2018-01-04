package com.finix.framework.rpc;

public interface Protocol {

	String getName();
	
	<T> Exporter<T> export( Provider<T> provider,URL serviceURL);
	
	<T> Refer<T> refer(Class<T> interfaceClass,URL referUrl,URL serviceUrl);
	
	void destroy();
}
