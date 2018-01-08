package com.finix.framework.spring.autoconfig;

import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.finix.framework.registry.Registry;
import com.finix.framework.registry.support.DirectRegistry;
import com.finix.framework.rpc.URL;
import com.finix.framework.util.NetUtil;

//TODO 加上配置属性定义
@Configuration
public class FinixRegistryAutoConfiguration {
    @Autowired
    private Environment environment;

    @Bean
    @ConditionalOnProperty(name = "finix.registry.name", havingValue = "direct", matchIfMissing = true)
    public Registry createDirectRegistry() {
        int port = Integer.parseInt(environment.getProperty("finix.registry.direct.port", "8080"));
        String basePath = environment.getProperty("finix.service.basePath", "finix");
        URL registryUrl = URL.builder()
                .host(NetUtil.getLocalIp())
                .port(port)
                .path(basePath)
                .parameters(new HashMap<>())
                .build();
        return new DirectRegistry(Collections.singletonList(registryUrl));
    }

//    @Bean
//    @ConditionalOnClass(FinixRadarRegistry.class)
//    @ConditionalOnProperty(name = "finix.registry.name", havingValue = "radar")
//    public Registry createRadarRegistry() {
//        String basePath = environment.getProperty("finix.service.basePath", "finix");
//
//        //radar服务地址
//        String regAddress = environment.getProperty("finix.registry.radar.address");
//        int regPort = Integer.parseInt(environment.getProperty("finix.registry.radar.port", "80"));
//
//        //radar用到的配置参数
//        String clusterName = environment.getProperty("finix.registry.clusterName", "default");
//        String appId = environment.getProperty("finix.registry.appId", "");
//        String appName = environment.getProperty("finix.registry.appName", "");
//        String instanceId = environment.getProperty("finix.registry.instanceId", "");
//
//        URL registryUrl = URL.builder()
//                .host(regAddress)
//                .port(regPort)
//                .path(basePath)
//                .parameters(new HashMap<>())
//                .build();
//        return new BeamRadarRegistry(registryUrl, instanceId, clusterName, appId, appName);
//    }

}