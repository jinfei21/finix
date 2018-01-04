package com.finix.framework.cluster.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.finix.framework.core.SpiBinder;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;
import com.google.common.collect.Lists;

@SpiBinder(name = "random")
public class RandomLoadBalance extends AbstractLoadBalance{

	@Override
	public String getAlgorithm() {
		return "random";
	}

	@Override
	protected Refer doSelect(Request request) {
		List<Refer> refers = getRefers();
		
		int idx = ThreadLocalRandom.current().nextInt();
		
		for(int i=0;i<refers.size();i++){
			Refer refer = refers.get(idx % refers.size());
			if(refer.isAvailable()){
				return refer;
			}
		}
		
		return null;
	}

	@Override
	protected List<Refer> doSelectToHolder(Request request) {

		List<Refer> refers = getRefers();
		List<Refer> refersHolder = Lists.newArrayList();
		int idx = ThreadLocalRandom.current().nextInt();
		
		for(int i=0;i<refers.size();i++){
			Refer refer = refers.get(idx%refers.size());
			if(refer.isAvailable()){
				refersHolder.add(refer);
			}
		}
		return refersHolder;
	}
	

}
