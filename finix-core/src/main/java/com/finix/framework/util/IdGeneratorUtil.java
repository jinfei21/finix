package com.finix.framework.util;

import com.finix.framework.common.IpIdGenerator;

public class IdGeneratorUtil {

    private static IpIdGenerator ipIdGenerator = new IpIdGenerator();

    /**
     * 获取 requestId
     *
     * @return
     */
    public static long getRequestId() {
        return ipIdGenerator.generateId();
    }
}
