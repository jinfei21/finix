package com.finix.framework.util;

import com.finix.framework.rpc.Request;

public class FinixFrameworkUtil {

    /**
     * 输出请求的关键信息： requestId=** interface=** method=**(**)
     *
     * @param request
     * @return
     */
    public static String toString(Request request) {
        return "requestId=" + request.getRequestId()
                + " interface=" + request.getInterfaceName()
                + " method=" + ReflectUtil.getMethodDesc(request.getMethodName(), request.getParamDesc());
    }

}
