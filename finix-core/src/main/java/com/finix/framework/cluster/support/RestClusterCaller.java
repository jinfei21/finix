package com.finix.framework.cluster.support;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections4.CollectionUtils;

import com.finix.framework.cluster.ClusterCaller;
import com.finix.framework.cluster.HaStrategy;
import com.finix.framework.cluster.LoadBalance;
import com.finix.framework.common.ClientConfig;
import com.finix.framework.common.Constants;
import com.finix.framework.common.URLParamType;
import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.protocol.FinixProtocol;
import com.finix.framework.registry.Registry;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.URL;
import com.finix.framework.util.NetUtil;

import lombok.Setter;

public class RestClusterCaller implements ClusterCaller{
	
	private ClientConfig clientConfig;
	
    @Setter
    private LoadBalance loadBalance;
    
    @Setter
    private HaStrategy haStrategy;
    
    private RegisterNotifyListener registerNotifyListener;
    
    private Registry registry;

    private URL referUrl;
    
    private String interfaceClass;
    
    private AtomicReference<List<Refer>> refers = new AtomicReference<>();

    private FinixProtocol protocol;
	
    public RestClusterCaller(ClientConfig clientConfig,Registry registry){
		this.clientConfig = clientConfig;
		this.registry = registry;
	}

	@Override
	public void init() {
		
        //注意初始化顺序
        initProtocol();
        initReferUrl();
        initLoadBalance();
        initHaStrategy();
        initRegisterNotifyListener();
        //最后初始化refers
        initRefers();
	}
	
    protected void initProtocol() {
        if (this.protocol == null) {
            throw new FinixFrameworkException("protocol can not be null.");
        }
        
        this.protocol = new FinixProtocol(null, null);
    }
    
    protected void initLoadBalance() {
        if (this.loadBalance == null) {
            this.loadBalance = new DefaultLoadBalanceFactory().getInstance(clientConfig.getLoadBalancer());
        }
    }

    protected void initHaStrategy() {
        if (this.haStrategy == null) {
            this.haStrategy = new DefaultHaStrategyFactory().getInstance(clientConfig.getHaStrategy());
        }
    }
    protected void initRegisterNotifyListener() {
        if (this.registerNotifyListener != null) {
            return;
        }
        this.registerNotifyListener = new RegisterNotifyListener(this);
        //订阅服务，根据不同注册中心的实现，这可能会去更新cluster的refers
        this.registry.subscribe(referUrl, this.registerNotifyListener);
    }
    
    protected void initReferUrl() {
        URL newReferUrl = URL.builder().protocol(this.protocol.getName())
                .host(NetUtil.getLocalIp())
                .port(0)
                .path(this.interfaceClass)
                .parameters(new HashMap<>())
                .build();
        newReferUrl.addParameter(URLParamType.nodeType.name(), Constants.NODE_TYPE_GATE);
        this.referUrl = newReferUrl;
    }
    
    protected void initRefers() {
        //如果refers没有初始化
        if (CollectionUtils.isEmpty(this.refers.get())) {
            List<URL> serviceUrls = this.registry.discover(referUrl);
            this.onRefresh(serviceUrls);
        }
    }

	@Override
	public void onRefresh(List<URL> serviceUrls) {
		
		
	}


	@Override
	public Response call(Request request) {
		
		return null;
	}
	
	@Override
	public List<Refer> getRefers() {
		return this.refers.get();
	}

	@Override
	public String getInterfaceClass() {
		return interfaceClass;
	}
	
	@Override
	public URL getReferUrl() {
		return this.referUrl;
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
}
