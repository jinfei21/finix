package com.finix.framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.finix.framework.exception.FinixAbstractException;
import com.finix.framework.exception.FinixBizException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionUtil {

    public static final StackTraceElement[] REMOTE_MOCK_STACK = new StackTraceElement[]{new StackTraceElement("remoteClass",
            "remoteMethod", "remoteFile", 1)};

    /**
     * 判定是否是业务方的逻辑抛出的异常
     * <p>
     * true: 来自业务方的异常
     * false: 来自框架本身的异常
     *
     * @param t
     * @return
     */
    public static boolean isBizException(Throwable t) {
        return t instanceof FinixBizException;
    }


    public static boolean isFinixException(Throwable t) {
        return t instanceof FinixAbstractException;
    }


    /**
     * 覆盖给定exception的stack信息，server端产生业务异常时调用此类屏蔽掉server端的异常栈。
     *
     * @param e
     */
    public static void setMockStackTrace(Throwable e) {
        if (e != null) {
            try {
                e.setStackTrace(REMOTE_MOCK_STACK);
            } catch (Exception e1) {
                log.warn("replace remote exception stack fail!" + e1.getMessage());
            }
        }
    }
    
    public static String getStackTraceString(Throwable e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
        try {
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e1) {
            return "ErrorInfoFromException";
        }finally{
        	try {
				sw.close();
				pw.close();
			} catch (IOException e1) {
			}
        }
    }
}
