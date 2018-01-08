package com.finix.framework.transport;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.URL;

/**
 * 空的ServletEndpoint，用于客户端调用构造协议
 */
public class NonServletEndpoint extends AbstractServletEndpoint {
    private static final String MESSAGE = "NonServletEndpoint can not invoke method.";

    public NonServletEndpoint() {
        super(null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public ConcurrentMap<String, Provider> getProviders() {
        return super.getProviders();
    }

    @Override
    public void init() {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public URL export(Provider provider, URL serviceUrl) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    protected URL doExport(Provider provider, URL serviceUrl) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        return super.getLastModified(req);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public void destroy() {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public String getInitParameter(String name) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public ServletConfig getServletConfig() {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public ServletContext getServletContext() {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public String getServletInfo() {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public void init(ServletConfig config) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public void log(String msg) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public void log(String message, Throwable t) {
        throw new FinixFrameworkException(MESSAGE);
    }

    @Override
    public String getServletName() {
        throw new FinixFrameworkException(MESSAGE);
    }
}