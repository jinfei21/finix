package com.finix.framework.transport;

import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.rpc.URL;

public class NonHttpClientFactory extends HttpClientFactory {
    
	@Override
    public HttpClient createHttpClient( URL serviceUrl) {
        throw new FinixFrameworkException("NonHttpClientFactory can not creare HttpClient.");
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}