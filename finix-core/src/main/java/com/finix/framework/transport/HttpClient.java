package com.finix.framework.transport;

import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;

public abstract class HttpClient implements Client {

    @Override
    public Response request(Request request) {
        return sendRequest(request);
    }

    public abstract Response sendRequest(Request request);
}
