package com.finix.framework.protocol;

import com.finix.framework.common.ClientConfig;
import com.finix.framework.rpc.Protocol;

public interface ProtocolFactory {

	Protocol getProtocol();
	
	void setClientConfig(ClientConfig clientConfig);
}
