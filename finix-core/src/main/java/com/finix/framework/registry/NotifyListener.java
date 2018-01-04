package com.finix.framework.registry;

import java.util.List;

import com.finix.framework.rpc.URL;

public interface NotifyListener {

    void notify(URL registryUrl, List<URL> serviceUrls);
}
