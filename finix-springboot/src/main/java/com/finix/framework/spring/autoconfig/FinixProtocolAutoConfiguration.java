package com.finix.framework.spring.autoconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.finix.framework.filter.Filter;
import com.finix.framework.protocol.FinixProtocol;
import com.finix.framework.protocol.ProtocolFilterDecorator;
import com.finix.framework.rpc.Protocol;
import com.finix.framework.transport.AbstractServletEndpoint;
import com.finix.framework.transport.HttpClientFactory;
import com.finix.framework.transport.NonHttpClientFactory;
import com.finix.framework.transport.NonServletEndpoint;

@Configuration
public class FinixProtocolAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(Protocol.class)
    public Protocol createProtocol() {
        AbstractServletEndpoint endpoint;
        HttpClientFactory clientFactory;
        //TODO 目前只支持beam协议，以后支持多种协议
        if (applicationContext.getBeansOfType(AbstractServletEndpoint.class).size() == 0) {
            endpoint = createNonServletEndpoint();
        } else {
            endpoint = applicationContext.getBean(AbstractServletEndpoint.class);
        }
        if (applicationContext.getBeansOfType(HttpClientFactory.class).size() == 0) {
            clientFactory = createNonHttpClientFactory();
        } else {
            clientFactory = applicationContext.getBean(HttpClientFactory.class);
        }
        Map<String, Filter> filterMaps = applicationContext.getBeansOfType(Filter.class);
        List<Filter> filters = new ArrayList<>(filterMaps.values());
        FinixProtocol beamProtocol = new FinixProtocol(endpoint, clientFactory);
        return new ProtocolFilterDecorator(beamProtocol, filters);
    }

    protected AbstractServletEndpoint createNonServletEndpoint() {
        return new NonServletEndpoint();
    }

    protected HttpClientFactory createNonHttpClientFactory() {
        return new NonHttpClientFactory();
    }
}