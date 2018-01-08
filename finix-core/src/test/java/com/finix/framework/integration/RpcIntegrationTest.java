package com.finix.framework.integration;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;

import com.finix.framework.cluster.ClusterCaller;
import com.finix.framework.cluster.support.DefaultClusterCaller;
import com.finix.framework.common.ClientConfig;
import com.finix.framework.proto.Helloworld;
import com.finix.framework.proto.Simple;
import com.finix.framework.proto.SimpleImpl;
import com.finix.framework.protocol.FinixProtocol;
import com.finix.framework.protocol.ProtocolFilterDecorator;
import com.finix.framework.proxy.ClusterInvocationHandler;
import com.finix.framework.proxy.JdkProxyFactory;
import com.finix.framework.registry.LocalRegistry;
import com.finix.framework.registry.Registry;
import com.finix.framework.registry.support.DirectRegistry;
import com.finix.framework.rpc.DefaultProvider;
import com.finix.framework.rpc.Exporter;
import com.finix.framework.rpc.Protocol;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.URL;
import com.finix.framework.transport.AbstractServletEndpoint;
import com.finix.framework.transport.FinixApacheHttpClientFactory;
import com.finix.framework.transport.JettyServletEndpoint;
import com.finix.framework.transport.NonServletEndpoint;
import com.finix.framework.util.NetUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcIntegrationTest {

    @Test
    public void testServer() throws InterruptedException {
        //初始化注册中心
        LocalRegistry registry = new LocalRegistry();

        // 生成servlet
        URL baseUrl = URL.builder()
                .protocol("finix")
                .host(NetUtil.getLocalIp())
                .port(8080)
                .path("/finix")
                .parameters(new HashMap<>()).build();
        JettyServletEndpoint servletEndpoint = new JettyServletEndpoint(baseUrl);

        //初始化协议
        FinixProtocol beamProtocol = new FinixProtocol(servletEndpoint, null);
        beamProtocol.setEndpoint(servletEndpoint);
        Protocol protocol = new ProtocolFilterDecorator(beamProtocol, null);

        //初始化service
        URL serviceUrl = URL.builder().parameters(new HashMap<>()).build();
        Simple simple = new SimpleImpl();
        Provider provider = new DefaultProvider<>(Simple.class, simple, serviceUrl);
        Exporter exporter = protocol.export(provider, serviceUrl);

        //注册服务
        registry.doRegister(exporter.getServiceUrl());

//        //打印metric
//        ConsoleReporter slf4jReporter = ConsoleReporter.forRegistry(MetricContext.getMetricRegistry()).build();
//        slf4jReporter.start(10, TimeUnit.SECONDS);

        //Server线程join过来
        servletEndpoint.getServer().join();

    }

    @Test
    public void testClient() throws ClassNotFoundException {

        //初始化注册中心
        URL registryUrl = URL.builder()
                .host(NetUtil.getLocalIp())
                .port(8080)
                .path("/beam")
                .parameters(new HashMap<>())
                .build();
        Registry registry = new DirectRegistry(Collections.singletonList(registryUrl));

        //初始化协议
        AbstractServletEndpoint servletEndpoint = new NonServletEndpoint();
        FinixProtocol beamProtocol = new FinixProtocol(servletEndpoint, new FinixApacheHttpClientFactory(new ClientConfig()));
        Protocol protocol = new ProtocolFilterDecorator(beamProtocol, null);

        //集群，referUrl只有配置有用
        URL referUrl = URL.builder().build();
        ClusterCaller cluster = new DefaultClusterCaller<>(Simple.class, protocol, referUrl, registry);
        cluster.init();

        //代理
        ClusterInvocationHandler<Simple> invocationHandler = new ClusterInvocationHandler<>(cluster);
        
        Simple proxy = new JdkProxyFactory().getProxy(Simple.class, invocationHandler);
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName("ppdai").build();
        Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);

        System.out.println(helloReply);
    }
}
