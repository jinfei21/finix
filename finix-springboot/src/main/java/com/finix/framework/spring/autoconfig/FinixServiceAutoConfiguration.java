package com.finix.framework.spring.autoconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import com.finix.framework.common.Constants;
import com.finix.framework.rpc.URL;
import com.finix.framework.transport.FinixServletEndpoint;
import com.finix.framework.util.NetUtil;

@Configuration
@Import({FinixServiceProcessor.class})
public class FinixServiceAutoConfiguration {
    @Autowired
    private Environment env;

    @Bean
    @ConditionalOnMissingBean(FinixServletEndpoint.class)
    public FinixServletEndpoint createServletEndpoint() {
        int port = Integer.parseInt(env.getProperty("server.port", "8080"));
        String path = env.getProperty("finix.service.basePath", "finix");
        URL baseUrl = URL.builder()
                .host(NetUtil.getLocalIp())
                .port(port)
                .path(path)
                .parameters(new HashMap<>()).build();
        return new FinixServletEndpoint(baseUrl);
    }

    @Bean
    public ServletRegistrationBean registerServlet(FinixServletEndpoint servletEndpoint) {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(servletEndpoint);
        Map<String, String> initParams = new HashMap<>();
        registrationBean.setInitParameters(initParams);
        List<String> urlMappings = new ArrayList<>();
        //先把开头的/删除，在加上/，保护下
        String path = Constants.PATH_SEPARATOR + StringUtils.removeStart(servletEndpoint.getBaseUrl().getPath(), Constants.PATH_SEPARATOR);
        path = StringUtils.removeEnd(path, Constants.PATH_SEPARATOR);
        urlMappings.add(path + "/*");
        registrationBean.setUrlMappings(urlMappings);
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }

}