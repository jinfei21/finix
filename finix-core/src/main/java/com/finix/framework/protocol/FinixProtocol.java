package com.finix.framework.protocol;

import com.finix.framework.rpc.AbstractRefer;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.URL;
import com.finix.framework.transport.AbstractServletEndpoint;
import com.finix.framework.transport.HttpClient;
import com.finix.framework.transport.HttpClientFactory;
import com.finix.framework.transport.NonServletEndpoint;

import lombok.Getter;
import lombok.Setter;

public class FinixProtocol  extends AbstractProtocol {
	
	
    @Getter
    @Setter
    private AbstractServletEndpoint endpoint;
    
    @Getter
    @Setter
    private HttpClientFactory clientFactory;
    
    public FinixProtocol(AbstractServletEndpoint endpoint, HttpClientFactory clientFactory) {
        if (endpoint == null) {
            endpoint = new NonServletEndpoint();
        }
        this.endpoint = endpoint;
        this.clientFactory = clientFactory;
    }

	@Override
	public Refer refer(String interfaceClass, URL referUrl, URL serviceUrl) {
		HttpClient httpClient = this.clientFactory.createHttpClient(serviceUrl);
		return new FinixRefer(interfaceClass,referUrl,serviceUrl,httpClient);
	}

	@Override
	public URL deploy(Provider provider, URL serviceUrl) {
		 return this.endpoint.export(provider, serviceUrl);
	}

	@Override
	public String getName() {
		return "finix";
	}
	
    public static class FinixRefer extends AbstractRefer {
        private HttpClient httpClient;

        public FinixRefer(String interfaceClass, URL referUrl, URL serviceUrl, HttpClient httpClient) {
            super(interfaceClass, referUrl, serviceUrl);
            this.httpClient = httpClient;
        }

        @Override
        protected Response doCall(Request request) {
            return httpClient.request(request);
        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }

    }
}
