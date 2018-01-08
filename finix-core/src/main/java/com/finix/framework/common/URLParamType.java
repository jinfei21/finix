package com.finix.framework.common;

public enum URLParamType {

    /** serialize **/    
    serialization("x-serialization", "protobuf.bin"),
    httpSchema("httpSchema", "http"),
    haStrategy("haStrategy", "failover"),
    loadbalance("loadbalance", "roundrobin"),
    accessLog("accessLog", true),
    retries("retries", 0),
    nodeType("nodeType", Constants.NODE_TYPE_SERVICE),
    transExceptionStack("transExceptionStack", true),
    basePath("basePath", "/"),
    parameterTypes("parameterTypes", null),
    requestId("requestId", "0"),
    clientHost("clientHost", ""),
    connectTimeout("connectTimeout",2000),
    socketTimeout("socketTimeout",35000),
    requestConnectTimeout("requestConnectTimeout",20),
    requestPayloadSize("requestPayloadSize", 0),
    responsePayloadSize("responsePayloadSize", 0),
    httpVersion("statusLine.protocolVersion", "1.1"),
    httpStatusCode("statusLine.code", 0),
    httpReasonPhrase("statusLine.reasonPhrase", ""),
    returnType("returnType", null),
    filter("filter", false),
	version("version",Constants.DEFAULT_VERSION);
	
    private String name;
    private String value;
    private long longValue;
    private int intValue;
    private boolean boolValue;

    private URLParamType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    private URLParamType(String name, long longValue) {
        this.name = name;
        this.value = String.valueOf(longValue);
        this.longValue = longValue;
    }

    private URLParamType(String name, int intValue) {
        this.name = name;
        this.value = String.valueOf(intValue);
        this.intValue = intValue;
    }

    private URLParamType(String name, boolean boolValue) {
        this.name = name;
        this.value = String.valueOf(boolValue);
        this.boolValue = boolValue;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public boolean getBooleanValue() {
        return boolValue;
    }
}
