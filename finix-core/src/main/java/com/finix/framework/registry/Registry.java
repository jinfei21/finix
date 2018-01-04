package com.finix.framework.registry;

import java.util.Collection;
import java.util.List;

import com.finix.framework.rpc.URL;

public interface Registry {

    URL getRegistryUrl();

    void subscribe(URL referUrl, NotifyListener listener);

    void unsubscribe(URL referUrl, NotifyListener listener);

    List<URL> discover(URL referUrl);

    void register(URL serviceUrl);

    void unregister(URL serviceUrl);

    Collection<URL> getRegisteredServiceUrls();

    void init();
}
