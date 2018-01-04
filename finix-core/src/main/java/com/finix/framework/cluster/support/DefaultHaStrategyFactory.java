package com.finix.framework.cluster.support;

import org.apache.commons.lang.StringUtils;

import com.finix.framework.cluster.HaStrategy;
import com.finix.framework.cluster.HaStrategyFactory;
import com.finix.framework.common.URLParamType;
import com.finix.framework.core.BinderSupporter;

public class DefaultHaStrategyFactory  implements HaStrategyFactory{
	

	@Override
	public HaStrategy getInstance(String name) {
		if(StringUtils.isBlank(name)){
			name = URLParamType.haStrategy.getValue();
		}
		return BinderSupporter.generate(HaStrategy.class, name);
	}

	
}
