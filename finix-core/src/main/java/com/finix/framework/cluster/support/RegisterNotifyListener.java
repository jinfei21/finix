package com.finix.framework.cluster.support;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.finix.framework.cluster.Cluster;
import com.finix.framework.registry.NotifyListener;
import com.finix.framework.rpc.URL;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册中心监听器实现，
 */
@Slf4j
@Getter
public class RegisterNotifyListener implements NotifyListener {

    private Cluster cluster;

    public RegisterNotifyListener(Cluster cluster) {
        this.cluster = cluster;
    }

    public void init() {
    }

    @Override
    public synchronized void notify(URL registryUrl, List<URL> serviceUrls) {
        if (CollectionUtils.isEmpty(serviceUrls)) {
            log.warn("ClusterSupport config change notify, urls is empty: registry={} service={}", registryUrl.getUri(),
                    cluster.getReferUrl().getIdentity());
        }
        log.info("ClusterSupport config change notify: registry={} service={} serviceUrls={}", registryUrl.getUri(),
                cluster.getReferUrl().getIdentity(), getIdentities(serviceUrls));

        cluster.onRefresh(serviceUrls);
    }


    private String getIdentities(List<URL> urls) {
        if (urls == null || urls.isEmpty()) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (URL url : urls) {
            builder.append(url.getIdentity()).append(",");
        }
        builder.setLength(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }

}