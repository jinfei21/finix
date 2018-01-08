package com.finix.framework.transport;

import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;

public interface Client {

    Response request(Request request);

}