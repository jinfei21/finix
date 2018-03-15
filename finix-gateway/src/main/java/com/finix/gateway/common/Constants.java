package com.finix.gateway.common;

public class Constants {

    public static final int DEFAULT_HEALTHY_THRESHOLD_VALUE = 2;
    public static final int DEFAULT_UNHEALTHY_THRESHOLD_VALUE = 2;
    public static final long DEFAULT_HEALTH_CHECK_INTERVAL = 5000L;
    public static final long DEFAULT_TIMEOUT_VALUE = 2000L;
    
    public static final int DEFAULT_RESPONSE_TIMEOUT_MILLIS = 1000;

    public static final String DEFAULT_SSL_PROVIDER = "JDK";
    
    public static final long DEFAULT_SESSION_TIMEOUT = 1000*60*30L;
    

    public static final int DEFAULT_MAX_CONNECTIONS_PER_HOST = 50;
    public static final int DEFAULT_MAX_PENDING_CONNECTIONS_PER_HOST = 25;
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 2000;
    public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 11000;
    
}
