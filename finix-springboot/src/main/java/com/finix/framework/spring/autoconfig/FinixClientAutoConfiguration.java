package com.finix.framework.spring.autoconfig;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.finix.framework.cluster.HaStrategyFactory;
import com.finix.framework.cluster.LoadBalanceFactory;
import com.finix.framework.cluster.support.DefaultHaStrategyFactory;
import com.finix.framework.cluster.support.DefaultLoadBalanceFactory;
import com.finix.framework.common.ClientConfig;
import com.finix.framework.serialize.ProtobufSerializationFactory;
import com.finix.framework.serialize.SerializationFactory;
import com.finix.framework.transport.FinixApacheHttpClientFactory;
import com.finix.framework.transport.HttpClientFactory;


@Configuration
public class FinixClientAutoConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    @ConditionalOnMissingBean
    public HttpClientFactory createHttpClientFactory(HttpClientConnectionManager clientConnectionManager,
                                                     SerializationFactory serializationFactory) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setConnectTimeout(Integer.parseInt(environment.getProperty("apache.httpclient.connectTimeout", "2000")));
        clientConfig.setSocketTimeout(Integer.parseInt(environment.getProperty("apache.httpclient.socketTimeout", "10000")));
        clientConfig.setRequestConnectTimeout(Integer.parseInt(environment.getProperty("apache.httpclient.requestConnectTimeout", "-1")));
        clientConfig.setRetryCount(Integer.parseInt(environment.getProperty("apache.httpclient.retryCount", "0")));
        FinixApacheHttpClientFactory factory = new FinixApacheHttpClientFactory(clientConfig);
        factory.setConnectionManager(clientConnectionManager);
        factory.setSerializationFactory(serializationFactory);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        int maxTotal = Integer.parseInt(environment.getProperty("apache.httpclient.connection.pool.maxTotal", "500"));
        int maxPerRoute = Integer.parseInt(environment.getProperty("apache.httpclient.connection.pool.maxPerRoute", "20"));
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        return connectionManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public SerializationFactory buildSerializationFactory() {
        ProtobufSerializationFactory serializationFactory = new ProtobufSerializationFactory();
        return serializationFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public LoadBalanceFactory buildLoadBalanceFactory() {
        DefaultLoadBalanceFactory loadBalanceFactory = new DefaultLoadBalanceFactory();
        return loadBalanceFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public HaStrategyFactory buildHaStrategyFactory() {
        DefaultHaStrategyFactory haStrategyFactory = new DefaultHaStrategyFactory();
        return haStrategyFactory;
    }
}