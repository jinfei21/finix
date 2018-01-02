package com.finix.framework.cluster.ha;

import com.finix.framework.cluster.LoadBalance;
import com.finix.framework.core.SpiBinder;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;

@SpiBinder(name = "failfast")
public class FailfastHaStrategy extends AbstractHaStrategy{

	@Override
	public String getStrategyName() {
		return "failfast";
	}

	@Override
	public Response call(Request request, LoadBalance loadBalance) {
		Refer refer = loadBalance.select(request);
		return refer.call(request);
	}

	
}
