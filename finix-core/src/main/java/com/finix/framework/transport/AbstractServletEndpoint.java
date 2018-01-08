package com.finix.framework.transport;

import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.StringUtils;

import com.finix.framework.common.Constants;
import com.finix.framework.common.URLParamType;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.URL;
import com.google.common.collect.Maps;

import lombok.Getter;

public abstract class AbstractServletEndpoint extends HttpServlet implements Endpoint {
	
    @Getter
    protected ConcurrentMap<String, Provider> providers = Maps.newConcurrentMap();

    @Getter
    protected URL baseUrl;

    AbstractServletEndpoint(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public URL export(Provider provider, URL serviceUrl) {
        URL newServiceUrl = doExport(provider, serviceUrl);
        String key = getProviderKey(provider.getInterface(), newServiceUrl.getParameter(URLParamType.version.name()));
        providers.put(key, provider);
        return newServiceUrl;
    }

    protected URL doExport(Provider provider, URL serviceUrl) {
        URL newServiceUrl = baseUrl.createCopy();
        String basePath = StringUtils.removeEnd(baseUrl.getPath(), Constants.PATH_SEPARATOR);
        newServiceUrl.setProtocol(serviceUrl.getProtocol());
        newServiceUrl.setPath(provider.getInterface());
        newServiceUrl.getParameters().putAll(serviceUrl.getParameters());
        newServiceUrl.addParameter(URLParamType.basePath.name(), basePath);
        return newServiceUrl;
    }

    protected String getProviderKey(String interfaceName, String version) {
        return StringUtils.isBlank(version) ? interfaceName : interfaceName + "?version=" + version;
    }
}