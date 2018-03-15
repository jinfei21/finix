package com.finix.gateway.common;

import java.io.Closeable;

public interface HttpClient extends Closeable {

	void sendRequest(GateHttpRequest request);
	
	default void close(){}

	default void registerStatusGauges(){}
}
