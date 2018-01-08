package com.finix.framework.transport;

import java.util.Map;

import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.URL;

public interface Endpoint {

    Map<String, Provider> getProviders();

    URL export(Provider provider, URL serviceUrl);

}