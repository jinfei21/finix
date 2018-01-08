package com.finix.framework.demo.server.restfulApi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finix.framework.registry.Registry;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.URL;
import com.finix.framework.transport.FinixServletEndpoint;

@RestController
public class RegistryController {

    @Autowired
    Registry registry;
    
    @Autowired
    FinixServletEndpoint endpoint;

    @RequestMapping("/unregistry")
    public String unRegistry() {
        Map<String, Provider> providers = endpoint.getProviders();
        for (Provider provider : providers.values()) {
            URL serviceUrl = provider.getServiceUrl();
            registry.unregister(serviceUrl);
        }
        return "OK";
    }

    @RequestMapping("/registry")
    public String registry() {
        Map<String, Provider> providers = endpoint.getProviders();
        for (Provider provider : providers.values()) {
            URL serviceUrl = provider.getServiceUrl();
            registry.register(serviceUrl);
        }
        return "OK";
    }
}
