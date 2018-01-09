package com.finix.framework.rpc;

import java.util.Map;

public interface Request {

    String getInterfaceName();

    String getMethodName();

    String getParamDesc();

    Object[] getArguments();

    String getReturnType();

    Map<String, String> getAttachments();

    void setAttachment(String name, String value);

    long getRequestId();

    int getRetries();

    void setRetries(int retries);
}