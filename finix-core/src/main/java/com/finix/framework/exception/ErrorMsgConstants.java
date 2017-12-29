package com.finix.framework.exception;

public class ErrorMsgConstants {

    // service error status 503
    public static final int SERVICE_DEFAULT_ERROR_CODE = 10001;
    
    
    // framework error
    public static final int FRAMEWORK_DEFAULT_ERROR_CODE = 20001;
    
    
    /**
     * service error start
     **/

    public static final FinixErrorMsg SERVICE_DEFAULT_ERROR = new FinixErrorMsg(503, SERVICE_DEFAULT_ERROR_CODE, "service error");
    
    
    /**
     * framework error start
     **/
    public static final FinixErrorMsg FRAMEWORK_DEFAULT_ERROR = new FinixErrorMsg(503, FRAMEWORK_DEFAULT_ERROR_CODE,"framework default error");
    
}
