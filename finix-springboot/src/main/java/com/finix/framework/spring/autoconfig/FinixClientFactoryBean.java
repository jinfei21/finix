package com.finix.framework.spring.autoconfig;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.finix.framework.cluster.HaStrategy;
import com.finix.framework.cluster.HaStrategyFactory;
import com.finix.framework.cluster.LoadBalance;
import com.finix.framework.cluster.LoadBalanceFactory;
import com.finix.framework.cluster.support.DefaultClusterCaller;
import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.proxy.ClusterInvocationHandler;
import com.finix.framework.proxy.JdkProxyFactory;
import com.finix.framework.registry.Registry;
import com.finix.framework.rpc.Protocol;
import com.finix.framework.rpc.URL;



public class FinixClientFactoryBean implements FactoryBean<Object>,
        ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Class<?> interfaceClass;

    @Override
    public Object getObject() throws Exception {
        Registry registry = getRegistry();
        Protocol protocol = getProtocol();
        URL referUrl = getReferUrl();

        DefaultClusterCaller<?> cluster = new DefaultClusterCaller<>(interfaceClass, protocol, referUrl, registry);

        LoadBalanceFactory loadBalanceFactory = applicationContext.getBean(LoadBalanceFactory.class);
        LoadBalance loadBalance = loadBalanceFactory.getInstance(null);
        cluster.setLoadBalance(loadBalance);

        HaStrategyFactory haStrategyFactory = applicationContext.getBean(HaStrategyFactory.class);
        HaStrategy haStrategy = haStrategyFactory.getInstance(null);
        cluster.setHaStrategy(haStrategy);

        cluster.init();
        ClusterInvocationHandler<?> invocationHandler = new ClusterInvocationHandler<>(cluster);
        //TODO 设置参数
        return new JdkProxyFactory().getProxy(interfaceClass, invocationHandler);
    }

    private Registry getRegistry() {
        //TODO 目前支持一个注册中心
        Map<String, Registry> registryMap = applicationContext.getBeansOfType(Registry.class);
        if (registryMap.size() > 1 || registryMap.size() == 0) {
            throw new FinixFrameworkException("Must has only one Registry bean, but has " + registryMap.size());
        }
        return new ArrayList<>(registryMap.values()).get(0);
    }

    private Protocol getProtocol() {
        //TODO 只支持一个BeamProtocol协议
        Map<String, Protocol> protocolMap = applicationContext.getBeansOfType(Protocol.class);
        if (protocolMap.size() > 1 || protocolMap.size() == 0) {
            throw new FinixFrameworkException("Must has only one BeamProtocol bean, but has " + protocolMap.size());
        }
        return new ArrayList<>(protocolMap.values()).get(0);
    }

    private URL getReferUrl() {
        //TODO 设置参数
        return URL.builder().parameters(new HashMap<>()).build();
    }

    @Override
    public Class<?> getObjectType() {
        return this.interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
}