package com.finix.framework.cluster.support;

import java.util.List;

import com.finix.framework.cluster.ClusterCaller;
import com.finix.framework.cluster.HaStrategy;
import com.finix.framework.cluster.LoadBalance;
import com.finix.framework.common.ClientConfig;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.URL;

import lombok.Setter;

public class RestClusterCaller implements ClusterCaller{
	
	private ClientConfig clientConfig;
	
    @Setter
    private LoadBalance loadBalance;
    
    @Setter
    private HaStrategy haStrategy;
    
    private RegisterNotifyListener registerNotifyListener;
    
	public RestClusterCaller(ClientConfig clientConfig){
	
		
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void destroy() {
		
		
	}

	@Override
	public void onRefresh(List<URL> serviceUrls) {
		
		
	}

	@Override
	public List<Refer> getRefers() {
		
		return null;
	}

	@Override
	public URL getReferUrl() {
		
		return null;
	}

	@Override
	public Response call(Request request) {
		
		return null;
	}

	@Override
	public String getInterfaceClass() {
		
		return null;
	}
	

}
