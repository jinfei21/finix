package com.finix.framework.common;

import lombok.Data;

@Data
public class ClientConfig {

    private int connectTimeout = 2000;
    private int socketTimeout = 10000;
    private int requestConnectTimeout = -1;
    private int poolMaxTotal = 3000;
    private int poolMaxPreRoute = 60;
    private int retryCount = 0;
    private boolean requestSentRetryEnabled = false;
    private String loadBalancer = URLParamType.loadbalance.getValue();
    private String haStrategy = URLParamType.haStrategy.getValue();
}
