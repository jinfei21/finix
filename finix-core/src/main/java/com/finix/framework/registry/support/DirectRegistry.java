package com.finix.framework.registry.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.finix.framework.common.Constants;
import com.finix.framework.common.URLParamType;
import com.finix.framework.registry.AbstractRegistry;
import com.finix.framework.registry.NotifyListener;
import com.finix.framework.rpc.URL;
import com.google.common.collect.Lists;

public class DirectRegistry extends AbstractRegistry{

	private Set<URL> subscribeUrls = Collections.synchronizedSet(new HashSet<>());
    private List<URL> directUrls;

    public DirectRegistry(List<URL> directUrls) {
    	super(null);
    	this.directUrls = directUrls;
    	init();
    }
    
    
	@Override
	public void init() {
		this.directUrls = Lists.newArrayList(directUrls);
		for(URL directUrl:directUrls){
			directUrl.setProtocol("direct");
		}
		this.registryUrl = directUrls.get(0);
	}

	@Override
	protected void doRegister(URL serviceUrl) {
		
	}

	@Override
	protected void doUnregister(URL serviceUrl) {
		
	}

	@Override
	protected void doSubscribe(URL referUrl, NotifyListener listener) {
		subscribeUrls.add(referUrl);
		listener.notify(this.getRegistryUrl(), doDiscover(referUrl));
		
	}

	@Override
	protected void doUnsubscribe(URL referUrl, NotifyListener listener) {
		subscribeUrls.remove(referUrl);
	}

	@Override
	protected List<URL> doDiscover(URL referUrl) {

		List<URL> serviceUrls = Lists.newArrayList();
		
		for(URL directUrl:directUrls){
			URL serviceUrl = directUrl.createCopy();
			serviceUrl.setProtocol(referUrl.getProtocol());
			serviceUrl.setPath(referUrl.getPath());
			serviceUrl.addParameter(URLParamType.nodeType.name(), Constants.NODE_TYPE_SERVICE);
            String basePath = Constants.PATH_SEPARATOR + StringUtils.removeStart(directUrl.getPath(), Constants.PATH_SEPARATOR);
            serviceUrl.addParameterIfAbsent(URLParamType.basePath.name(), basePath);
            serviceUrls.add(serviceUrl);
		}
		
		return serviceUrls;
	}

	
}
