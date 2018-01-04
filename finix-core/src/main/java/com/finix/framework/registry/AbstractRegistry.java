package com.finix.framework.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.finix.framework.rpc.URL;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public  abstract class AbstractRegistry implements Registry {

	protected URL registryUrl;
	protected Map<URL,Set<URL>> subscribeCachedUrls = Maps.newConcurrentMap();
	protected Set<URL> registeredServiceUrls = Collections.synchronizedSet(new HashSet<URL>());
    protected String registryClassName = this.getClass().getSimpleName();

	public AbstractRegistry(URL registryUrl){
		if(registryUrl != null){
			this.registryUrl = registryUrl.createCopy();
		}
	}
	
	@Override
	public void register(URL serviceUrl) {
		if(serviceUrl == null){
            log.warn("[{}] register with malformed param, serviceUrl is null", registryClassName);
            return;		
        }
		
        log.info("[{}] serviceUrl ({}) will register to Registry [{}]", registryClassName, serviceUrl, registryUrl.getIdentity());

        doRegister(serviceUrl.createCopy());
        registeredServiceUrls.add(serviceUrl);
		
	}
	


	@Override
	public void unregister(URL serviceUrl) {
		if(serviceUrl == null){
            log.warn("[{}] unregister with malformed param, serviceUrl is null", registryClassName);
            return;
		}
		
        log.info("[{}] serviceUrl ({}) will unregister to Registry [{}]", registryClassName, serviceUrl, registryUrl.getIdentity());
        doUnregister(serviceUrl.createCopy());
        registeredServiceUrls.remove(serviceUrl);		
	}
	
	@Override
	public void subscribe(URL referUrl, NotifyListener listener) {
		if(referUrl == null || listener == null){
            log.warn("[{}] subscribe with malformed param, referUrl:{}, listener:{}", registryClassName, referUrl, listener);
            return;
		}
        log.info("[{}] Listener ({}) will subscribe to referUrl ({}) in Registry [{}]", registryClassName, listener, referUrl, registryUrl.getIdentity());
        doSubscribe(referUrl.createCopy(), listener);
	}

	@Override
	public void unsubscribe(URL referUrl, NotifyListener listener) {
        if (referUrl == null || listener == null) {
            log.warn("[{}] unsubscribe with malformed param, referUrl:{}, listener:{}", registryClassName, referUrl, listener);
            return;
        }
        log.info("[{}] Listener ({}) will unsubscribe from referUrl ({}) in Registry [{}]", registryClassName, listener, referUrl,
                registryUrl.getIdentity());
        doUnsubscribe(referUrl.createCopy(), listener);
		
	}
	
	@Override
	public List<URL> discover(URL referUrl) {
		List<URL> urlsDiscovered = doDiscover(referUrl);
		List<URL> serviceUrlsCopy = Lists.newArrayList();
		
		for(URL tempUrl:urlsDiscovered){
			serviceUrlsCopy.add(tempUrl);
		}
		
		return serviceUrlsCopy;
	}
	
	@Override
	public URL getRegistryUrl() {
		return this.registryUrl;
	}
	
	@Override
	public Collection<URL> getRegisteredServiceUrls() {
		return this.registeredServiceUrls;
	}
	
	protected List<URL> getSubscribeCachedUrls(URL referUrl){
		Set<URL> serviceUrls = subscribeCachedUrls.get(referUrl);
		
		if(serviceUrls == null || serviceUrls.size() == 0){
			return Lists.newArrayList();
		}
		
		List<URL> serviceUrlsCopy = Lists.newArrayList();
		for(URL tempUrl:serviceUrls){
			serviceUrlsCopy.add(tempUrl.createCopy());
		}
		
		return serviceUrlsCopy;
	}
	
	protected void notify(URL referUrl,NotifyListener listener,List<URL> serviceUrls){
		if(listener == null || serviceUrls == null){
			return;
		}
		
		HashSet<URL> temp = Sets.newHashSet();
		
		for(URL serviceUrl:serviceUrls){
			temp.add(serviceUrl.createCopy());
		}
		
		this.subscribeCachedUrls.put(referUrl, temp);
		listener.notify(getRegistryUrl(), Lists.newArrayList(temp));
	}
	
    protected abstract void doRegister(URL serviceUrl);

    protected abstract void doUnregister(URL serviceUrl);

    protected abstract void doSubscribe(URL referUrl, NotifyListener listener);

    protected abstract void doUnsubscribe(URL referUrl, NotifyListener listener);

    protected abstract List<URL> doDiscover(URL referUrl);
	
}
