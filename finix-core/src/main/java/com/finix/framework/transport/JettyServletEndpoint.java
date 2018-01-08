package com.finix.framework.transport;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.finix.framework.common.Constants;
import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.rpc.URL;

import lombok.Getter;

public class JettyServletEndpoint extends FinixServletEndpoint {

    @Getter
    private Server server;

    public JettyServletEndpoint(URL baseUrl) {
        super(baseUrl);
        initJettyServer();
    }

    private void initJettyServer() {
        server = new Server(baseUrl.getPort());
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(DefaultServlet.class, "/");
        ServletHolder servletHolder = new ServletHolder(this);
        String pathSpec = StringUtils.removeEnd(baseUrl.getPath(), Constants.PATH_SEPARATOR) + Constants.PATH_SEPARATOR + "*";
        servletContextHandler.addServlet(servletHolder, pathSpec);
        try {
            server.start();
        } catch (Exception e) {
            throw new FinixFrameworkException("Start jetty server error.", e);
        }
    }
}