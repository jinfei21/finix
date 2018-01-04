package com.finix.framework.registry;

import com.finix.framework.rpc.URL;

public interface RegistryFactory {

    Registry getRegistry(URL registryUrl);
}
