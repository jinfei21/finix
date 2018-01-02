package com.finix.framework.cluster;

import java.util.List;

import com.finix.framework.core.Scope;
import com.finix.framework.core.Spi;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;

@Spi(scope = Scope.PROTOTYPE)
public interface LoadBalance {

	void onRefresh(List<Refer> refers);
	
	Refer select(Request request);
	
	List<Refer> selectToHolder(Request request);
	
    String getAlgorithm();

}
