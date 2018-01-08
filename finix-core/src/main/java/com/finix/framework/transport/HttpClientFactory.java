package com.finix.framework.transport;


import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.URL;

public abstract class HttpClientFactory implements ClientFactory {

    public abstract HttpClient createHttpClient(URL serviceUrl);

    @Override
    public Client createClient( URL serviceUrl) {
        return new Client() {
            @Override
            public Response request(Request request) {
                HttpClient httpClient = createHttpClient(serviceUrl);
                return httpClient.request(request);
            }
        };
    }
}