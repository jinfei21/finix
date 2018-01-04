package com.finix.framework.rpc;

public interface Exporter<T> {

	URL getServiceUrl();
	Provider<T> getProvider();
}
