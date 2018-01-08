package com.finix.framework.cluster.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;

import com.finix.framework.cluster.ClusterCaller;
import com.finix.framework.cluster.HaStrategy;
import com.finix.framework.cluster.LoadBalance;
import com.finix.framework.common.Constants;
import com.finix.framework.common.URLParamType;
import com.finix.framework.exception.FinixAbstractException;
import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.filter.DefaultFiltersSupport;
import com.finix.framework.filter.Filter;
import com.finix.framework.protocol.ProtocolFilterDecorator;
import com.finix.framework.registry.Registry;
import com.finix.framework.rpc.Protocol;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.ReferSupports;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.URL;
import com.finix.framework.util.ExceptionUtil;
import com.finix.framework.util.NetUtil;
import com.google.common.collect.Lists;

import lombok.Setter;

public class DefaultClusterCaller<T> implements ClusterCaller{

    private Class<T> interfaceClass;
    private Protocol protocol;
    private URL referUrl;
    private Registry registry;

    private AtomicReference<List<Refer>> refers = new AtomicReference<>();

    private RegisterNotifyListener registerNotifyListener;
    
    @Setter
    private LoadBalance loadBalance;
    
    @Setter
    private HaStrategy haStrategy;
    
    @Setter
    private List<Filter> filters;
	
    
    public DefaultClusterCaller(Class<T> interfaceClass, Protocol protocol, URL referUrl, Registry registry) {
        this.interfaceClass = interfaceClass;
        this.protocol = protocol;
        this.referUrl = referUrl;
        this.registry = registry;
    }
    
    protected Protocol getDecorateProtocol(Protocol protocol) {
        if (protocol instanceof ProtocolFilterDecorator) {
            return protocol;
        }
        return new ProtocolFilterDecorator(protocol, filters);
    }

	
	@Override
	public String getInterfaceClass() {
		return interfaceClass.getName();
	}

	@Override
	public void init() {
        //注意初始化顺序
        initFilters();
        initProtocol();
        initReferUrl();
        initLoadBalance();
        initHaStrategy();
        initRegisterNotifyListener();
        //最后初始化refers
        initRefers();
	}
	
    protected void initFilters() {
        if (this.filters == null) {
            this.filters = DefaultFiltersSupport.getDefaultFilters();
        }
    }

    protected void initProtocol() {
        if (this.protocol == null) {
            throw new FinixFrameworkException("protocol can not be null.");
        }
        this.protocol = getDecorateProtocol(this.protocol);
    }

    protected void initReferUrl() {
        URL newReferUrl = URL.builder().protocol(this.protocol.getName())
                .host(NetUtil.getLocalIp())
                .port(0)
                .path(this.interfaceClass.getName())
                .parameters(referUrl.getParameters() == null ? new HashMap<>() : referUrl.getParameters())
                .build();
        newReferUrl.addParameter(URLParamType.nodeType.name(), Constants.NODE_TYPE_REFER);
        this.referUrl = newReferUrl;
    }

    protected void initRegisterNotifyListener() {
        if (this.registerNotifyListener != null) {
            return;
        }
        this.registerNotifyListener = new RegisterNotifyListener(this);
        //订阅服务，根据不同注册中心的实现，这可能会去更新cluster的refers
        this.registry.subscribe(referUrl, this.registerNotifyListener);
    }

    protected void initRefers() {
        //如果refers没有初始化
        if (CollectionUtils.isEmpty(this.refers.get())) {
            List<URL> serviceUrls = this.registry.discover(referUrl);
            this.onRefresh(serviceUrls);
        }
    }

    protected void initLoadBalance() {
        if (this.loadBalance == null) {
            this.loadBalance = new DefaultLoadBalanceFactory().getInstance(URLParamType.loadbalance.getValue());
        }
    }

    protected void initHaStrategy() {
        if (this.haStrategy == null) {
            this.haStrategy = new DefaultHaStrategyFactory().getInstance(URLParamType.haStrategy.getValue());
        }
    }

	@Override
	public void destroy() {
        //销毁所有的refer
        if (this.refers.get() != null) {
            for (Refer refer : refers.get()) {
                refer.destroy();
            }
        }
	}

	@Override
	public void onRefresh(List<URL> serviceUrls) {
        List<Refer> newRefers = Lists.newArrayList();
        List<Refer> oldRefers = this.refers.get() == null ? Lists.newArrayList(): this.refers.get();
        for (URL serviceUrl : serviceUrls) {
            if (!serviceUrl.canServe(referUrl)) {
                continue;
            }
            Refer refer = getExistingRefer(serviceUrl);
            if (refer == null) {
                // serverURL, referURL的配置会被serverURL的配置覆盖
                URL referURL = serviceUrl.createCopy();
                referURL.addParameters(this.referUrl.getParameters());
                refer = protocol.refer(this.interfaceClass.getName(), referURL, serviceUrl);
            }
            if (refer != null) {
                newRefers.add(refer);
            }
        }
        this.refers.set(newRefers);
        loadBalance.onRefresh(newRefers);

        //关闭多余的refer
        List<Refer> delayDestroyRefers = new ArrayList<>();
        for (Refer refer : oldRefers) {
            if (newRefers.contains(refer)) {
                continue;
            }
            delayDestroyRefers.add(refer);
        }
        if (!delayDestroyRefers.isEmpty()) {
            ReferSupports.delayDestroy(delayDestroyRefers);
        }
	}
	

    /**
     * 一个serviceUrl对应一个refer,Url要完全相同
     *
     * @param serviceUrl
     * @return
     */
    private Refer getExistingRefer(URL serviceUrl) {
        if (refers.get() == null) {
            return null;
        }
        for (Refer refer : refers.get()) {
            if (ObjectUtils.equals(serviceUrl, refer.getServiceUrl())) {
                return refer;
            }
        }
        return null;
    }


	@Override
	public List<Refer> getRefers() {
		return this.refers.get();
	}

	@Override
	public URL getReferUrl() {
		return this.referUrl;
	}

	@Override
	public Response call(Request request) {
        try {
            return haStrategy.call(request, loadBalance);
        } catch (Exception e) {
            throw callFalse(request, e);
        }
	}
	
    protected RuntimeException callFalse(Request request, Exception cause) {
        if (ExceptionUtil.isBizException(cause)) {
            return (RuntimeException) cause;
        }

        if (cause instanceof FinixAbstractException) {
            return (FinixAbstractException) cause;
        } else {
            return new FinixServiceException(String.format("ClusterSpi Call false for request: %s", request), cause);
        }
    }
}
