package com.finix.framework.cluster;

import com.finix.framework.core.Scope;
import com.finix.framework.core.Spi;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;

@Spi(scope = Scope.PROTOTYPE)
public interface HaStrategy {

	String getStrategyName();
	
	Response call(Request request,LoadBalance loadBalance);
}
