package com.finix.framework.rpc;

import java.util.Map;

public interface Response {

    /**
     * <pre>
	 * 		如果 request 正常处理，那么会返回 Object value，而如果 request 处理有异常，那么 getValue 会抛出异常
	 * </pre>
     * 
     * @throws RuntimeException
     * @return
     */
    Object getValue();

    /**
     * 如果request处理有异常，那么调用该方法return exception 如果request还没处理完或者request处理正常，那么return null
     * 
     * <pre>
	 * 		该方法不会阻塞，无论该request是处理中还是处理完成
	 * </pre>
     * 
     * @return
     */
    Exception getException();

    /**
     * 与 Request 的 requestId 相对应
     * 
     * @return
     */
    long getRequestId();

    /**
     * 业务处理时间
     * 
     * @return
     */
    long getProcessTime();

    /**
     * 业务处理时间
     * 
     * @param time
     */
    void setProcessTime(long time);

    int getTimeout();

    Map<String, String> getAttachments();

    void setAttachment(String key, String value);

    // 获取rpc协议版本，可以依据协议版本做返回值兼容
    void setRpcProtocolVersion(byte rpcProtocolVersion);

    byte getRpcProtocolVersion();
}