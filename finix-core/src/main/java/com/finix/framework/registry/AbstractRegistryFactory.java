package com.finix.framework.registry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.finix.framework.exception.ErrorMsgConstants;
import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.rpc.URL;

public abstract class AbstractRegistryFactory implements RegistryFactory {

    private static ConcurrentHashMap<String, Registry> registries = new ConcurrentHashMap<String, Registry>();

    private static final ReentrantLock lock = new ReentrantLock();

    protected String getRegistryUri(URL url) {
        String registryUri = url.getUri();
        return registryUri;
    }

    @Override
    public Registry getRegistry(URL url) {
        String registryUri = getRegistryUri(url);
        try {
            lock.lock();
            Registry registry = registries.get(registryUri);
            if (registry != null) {
                return registry;
            }
            registry = createRegistry(url);
            if (registry == null) {
                throw new FinixFrameworkException("Create registry false for url:" + url,ErrorMsgConstants.FRAMEWORK_INIT_ERROR);
            }
            registries.put(registryUri, registry);
            return registry;
        } catch (Exception e) {
            throw new FinixFrameworkException("Create registry false for url:" + url, e, ErrorMsgConstants.FRAMEWORK_INIT_ERROR);
        } finally {
            lock.unlock();
        }
    }

    protected abstract Registry createRegistry(URL url);
}