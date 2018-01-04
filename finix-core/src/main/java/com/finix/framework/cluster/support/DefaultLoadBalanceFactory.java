package com.finix.framework.cluster.support;

import org.apache.commons.lang.StringUtils;

import com.finix.framework.cluster.LoadBalance;
import com.finix.framework.cluster.LoadBalanceFactory;
import com.finix.framework.common.URLParamType;
import com.finix.framework.core.BinderSupporter;

public class DefaultLoadBalanceFactory implements LoadBalanceFactory{

	@Override
	public LoadBalance newInstance(String name) {

		if(StringUtils.isBlank(name)){
			name = URLParamType.loadbalance.getValue();
		}
		
		return BinderSupporter.generate(LoadBalance.class, name);
	}

}
