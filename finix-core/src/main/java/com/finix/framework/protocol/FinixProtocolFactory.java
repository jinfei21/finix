package com.finix.framework.protocol;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.finix.framework.common.ClientConfig;
import com.finix.framework.common.Constants;
import com.finix.framework.rpc.Protocol;
import com.finix.framework.rpc.URL;
import com.finix.framework.transport.AbstractServletEndpoint;
import com.finix.framework.transport.FinixApacheHttpClientFactory;
import com.finix.framework.transport.FinixServletEndpoint;
import com.finix.framework.transport.HttpClientFactory;
import com.finix.framework.util.NetUtil;

import lombok.Getter;
import lombok.Setter;

public class FinixProtocolFactory implements ProtocolFactory {
	
	
    @Getter
    @Setter
    private AbstractServletEndpoint endpoint;
	
    @Getter
    @Setter
    private FinixApacheHttpClientFactory clientFactory;
    
    private AtomicReference<FinixProtocol> protocolRef;

    private FinixProtocolFactory() {
        init();
    }

    private FinixProtocolFactory(AbstractServletEndpoint endpoint, FinixApacheHttpClientFactory clientFactory) {
        this.endpoint = endpoint;
        this.clientFactory = clientFactory;
        init();
    }
	
    public void init() {
    	if(this.endpoint == null){
            URL url = URL.builder().host(NetUtil.getLocalIp())
                    .port(Constants.DEFAULT_PORT)
                    .protocol(Constants.DEFAULT_PROTOCOL)
                    .path(Constants.PATH_SEPARATOR)
                    .parameters(new HashMap<>())
                    .build();
            this.endpoint = new FinixServletEndpoint(url);
    	}
    	
    	if(this.clientFactory == null){
    		this.clientFactory = new FinixApacheHttpClientFactory(new ClientConfig());
    	}
    	this.protocolRef.set(new FinixProtocol(endpoint,clientFactory));
    }
	
    
	
	public static FinixProtocolFactory getInstance() {
		return FinixProtocolFactoryHolder.INSTANCE;
	}

    private static class FinixProtocolFactoryHolder {
        private static final FinixProtocolFactory INSTANCE = new FinixProtocolFactory();
    }

	@Override
	public Protocol getProtocol() {
		return this.protocolRef.get();
	}

	@Override
	public void setClientConfig(ClientConfig clientConfig) {
		this.clientFactory.setClientConfig(clientConfig);
	}
}
