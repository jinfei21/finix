package com.finix.framework.cluster.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.http.HttpEntity;

import com.finix.framework.cluster.ClusterCaller;
import com.finix.framework.cluster.HaStrategy;
import com.finix.framework.cluster.LoadBalance;
import com.finix.framework.common.ClientConfig;
import com.finix.framework.common.Constants;
import com.finix.framework.common.URLParamType;
import com.finix.framework.exception.FinixAbstractException;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.protocol.FinixProtocolFactory;
import com.finix.framework.registry.Registry;
import com.finix.framework.rpc.DefaultRequest;
import com.finix.framework.rpc.Protocol;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.ReferSupports;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.RpcContext;
import com.finix.framework.rpc.URL;
import com.finix.framework.util.ExceptionUtil;
import com.finix.framework.util.IdGeneratorUtil;
import com.finix.framework.util.NetUtil;
import com.google.common.collect.Lists;

import lombok.Setter;

public class RestClusterCaller implements ClusterCaller{
		
    @Setter
    private LoadBalance loadBalance;
    
    @Setter
    private HaStrategy haStrategy;
    
    private RegisterNotifyListener registerNotifyListener;
    
    private Registry registry;

    private AtomicReference<URL> referUrlRef = new AtomicReference<>();
    
    private String interfaceClass;
    
    private AtomicReference<List<Refer>> refersRef = new AtomicReference<>();

    private Protocol protocol;
	
    public RestClusterCaller(String interfaceClass,ClientConfig clientConfig,Registry registry){
		this.interfaceClass = interfaceClass;
		this.registry = registry;
		initProtocol();
		initReferUrl(clientConfig);
        if (this.loadBalance == null) {
            this.loadBalance = new DefaultLoadBalanceFactory().getInstance(clientConfig.getLoadBalancer());
        }
        
        if (this.haStrategy == null) {
            this.haStrategy = new DefaultHaStrategyFactory().getInstance(clientConfig.getHaStrategy());
        }
        init();
	}

	@Override
	public void init() {
		
        //注意初始化顺序
        initRegisterNotifyListener();
        //最后初始化refers
        initRefers();
	}
	
	public void setClientConfig(ClientConfig clientConfig){
		initReferUrl(clientConfig);
		FinixProtocolFactory.getInstance().setClientConfig(clientConfig);
	}
	
    protected void initProtocol() {
        this.protocol = FinixProtocolFactory.getInstance().getProtocol();
    }
    
    
    protected void initRegisterNotifyListener() {
        if (this.registerNotifyListener != null) {
            return;
        }
        this.registerNotifyListener = new RegisterNotifyListener(this);
        //订阅服务，根据不同注册中心的实现，这可能会去更新cluster的refers
        this.registry.subscribe(referUrlRef.get(), this.registerNotifyListener);
    }
    
    protected void initReferUrl(ClientConfig clientConfig) {
        URL newReferUrl = URL.builder().protocol(this.protocol.getName())
                .host(NetUtil.getLocalIp())
                .port(0)
                .path(this.interfaceClass)
                .parameters(new HashMap<>())
                .build();
        newReferUrl.addParameter(URLParamType.nodeType.getName(), Constants.NODE_TYPE_GATE);
        newReferUrl.addParameter(URLParamType.socketTimeout.getName(), String.valueOf(clientConfig.getSocketTimeout()));
        newReferUrl.addParameter(URLParamType.connectTimeout.getName(), String.valueOf(clientConfig.getConnectTimeout()));
        newReferUrl.addParameter(URLParamType.requestConnectTimeout.getName(), String.valueOf(clientConfig.getRequestConnectTimeout()));

        this.referUrlRef.set(newReferUrl);
    }
    
    protected void initRefers() {
        //如果refers没有初始化
        if (CollectionUtils.isEmpty(this.refersRef.get())) {
            List<URL> serviceUrls = this.registry.discover(referUrlRef.get());
            this.onRefresh(serviceUrls);
        }
    }

	@Override
	public void onRefresh(List<URL> serviceUrls) {
		
        List<Refer> newRefers = Lists.newArrayList();
        List<Refer> oldRefers = this.refersRef.get() == null ? Lists.newArrayList(): this.refersRef.get();
        for (URL serviceUrl : serviceUrls) {
            if (!serviceUrl.canServe(referUrlRef.get())) {
                continue;
            }
            Refer refer = getExistingRefer(serviceUrl);
            if (refer == null) {
                // serverURL, referURL的配置会被serverURL的配置覆盖
                URL referURL = serviceUrl.createCopy();
                referURL.addParameters(this.referUrlRef.get().getParameters());
                refer = protocol.refer(this.interfaceClass, referURL, serviceUrl);
            }
            if (refer != null) {
                newRefers.add(refer);
            }
        }
        this.refersRef.set(newRefers);
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


	@Override
	public Response call(Request request) {
		
        try {        	
            return haStrategy.call(request, loadBalance);
        } catch (Exception e) {
            throw callFalse(request, e);
        }
	}
	
	public Response call(String methodName,HttpEntity data){
		DefaultRequest  request = new DefaultRequest();
		request.setRequestId(IdGeneratorUtil.getRequestId());
		request.setInterfaceName(this.interfaceClass);
		request.setMethodName(methodName);
		request.setParamDesc(null);
		request.setArguments(new Object[] {data});
		request.setReturnType(null);		
		RpcContext rpcContext = RpcContext.getContext();
		rpcContext.putAttribute(URLParamType.socketTimeout.getName(),this.referUrlRef.get().getIntParameter(URLParamType.socketTimeout.getName(), URLParamType.socketTimeout.getIntValue()));
		rpcContext.putAttribute(URLParamType.requestConnectTimeout.getName(), this.referUrlRef.get().getIntParameter(URLParamType.requestConnectTimeout.getName(), URLParamType.requestConnectTimeout.getIntValue()));
		rpcContext.putAttribute(URLParamType.connectTimeout.getName(), this.referUrlRef.get().getIntParameter(URLParamType.connectTimeout.getName(), URLParamType.connectTimeout.getIntValue()));

		request.setAttachment(URLParamType.version.getName(), this.referUrlRef.get().getVersion());
		request.setAttachment(URLParamType.serialization.getName(), "protobuf.json");
		request.setAttachment(URLParamType.stream.getName(), URLParamType.stream.getValue());
		return call(request);
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
	
	
    /**
     * 一个serviceUrl对应一个refer,Url要完全相同
     *
     * @param serviceUrl
     * @return
     */
    private Refer getExistingRefer(URL serviceUrl) {
        if (refersRef.get() == null) {
            return null;
        }
        for (Refer refer : refersRef.get()) {
            if (ObjectUtils.equals(serviceUrl, refer.getServiceUrl())) {
                return refer;
            }
        }
        return null;
    }
    
	@Override
	public List<Refer> getRefers() {
		return this.refersRef.get();
	}

	@Override
	public String getInterfaceClass() {
		return interfaceClass;
	}
	
	@Override
	public URL getReferUrl() {
		return this.referUrlRef.get();
	}

	@Override
	public void destroy() {
        //销毁所有的refer
        if (this.refersRef.get() != null) {
            for (Refer refer : refersRef.get()) {
                refer.destroy();
            }
        }		
	}
}
