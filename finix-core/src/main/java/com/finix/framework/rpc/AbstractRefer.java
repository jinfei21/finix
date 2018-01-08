package com.finix.framework.rpc;

import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.util.FinixFrameworkUtil;

public abstract class AbstractRefer implements Refer{

	private String interfaceClass;
	
	private URL referUrl;
	
	private URL serviceUrl;
	
	public AbstractRefer(String interfaceClass,URL referUrl,URL serviceUrl){
		this.interfaceClass = interfaceClass;
		this.referUrl = referUrl;
		this.serviceUrl = serviceUrl;
	}
	
	@Override
	public Response call(Request request) {
		if(!isAvailable()){
            throw new FinixFrameworkException(this.getClass().getSimpleName() + " call Error: node is not available, serviceUrl=" + serviceUrl.getUri()
            + " " + FinixFrameworkUtil.toString(request));
		}
		return doCall(request);
	}
	
	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public URL getReferUrl() {
		return this.referUrl;
	}

	@Override
	public URL getServiceUrl() {
		return this.serviceUrl;
	}
	
	@Override
	public String getInterface() {
		return this.interfaceClass;
	}

	protected abstract Response doCall(Request request);

}
