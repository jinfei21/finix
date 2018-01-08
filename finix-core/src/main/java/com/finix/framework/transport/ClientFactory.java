package com.finix.framework.transport;

import com.finix.framework.rpc.URL;

public interface ClientFactory {

    void init();

    void destroy();

    Client createClient(URL serviceUrl);

}