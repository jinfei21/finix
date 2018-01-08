package com.finix.framework.protocol;

import com.finix.framework.rpc.Protocol;

public interface ProtocolFactory {

	Protocol newInstance();
}
