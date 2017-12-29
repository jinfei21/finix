package com.finix.framework.rpc;

import java.util.Map;

public interface Request {

    /**
     * 
     * service interface
     * 
     * @return
     */
    String getInterfaceName();

    /**
     * service method name
     * 
     * @return
     */
    String getMethodName();

    /**
     * service method param desc (sign)
     * 
     * @return
     */
    String getParamtersDesc();

    /**
     * service method param
     * 
     * @return
     */
    Object[] getArguments();

    /**
     * get framework param
     * 
     * @return
     */
    Map<String, String> getAttachments();

    /**
     * set framework param
     * 
     * @return
     */
    void setAttachment(String name, String value);

    /**
     * request id
     * 
     * @return
     */
    long getRequestId();

    /**
     * retries
     * 
     * @return
     */
    int getRetries();

    /**
     * set retries
     */
    void setRetries(int retries);

    // 获取rpc协议版本，可以依据协议版本做返回值兼容
    void setRpcProtocolVersion(byte rpcProtocolVersion);

    byte getRpcProtocolVersion();
}