package com.finix.framework.cluster.loadbalance;

import java.util.List;

import com.finix.framework.core.SpiBinder;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;

@SpiBinder(name = "roundrobin")
public class RoundRobinLoadBalance extends AbstractLoadBalance{

	@Override
	public String getAlgorithm() {
		return "roundrobin";
	}

	@Override
	protected Refer doSelect(Request request) {

		return null;
	}

	@Override
	protected List<Refer> doSelectToHolder(Request request) {

		return null;
	}

	
}
