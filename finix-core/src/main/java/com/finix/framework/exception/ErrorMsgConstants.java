package com.finix.framework.exception;

public class ErrorMsgConstants {

    public static final int SUCCESS = 0;

    
    // service error status 503
    public static final int SERVICE_DEFAULT_ERROR_CODE = 10001;
    public static final int SERVICE_TIMEOUT_ERROR_CODE = 10003;

    // service error status 404
    public static final int SERVICE_UNFOUND_ERROR_CODE = 10101;
    
    // service error status 3XX
    public static final int SERVICE_REDIRECT_ERROR_CODE = 10300;
    // service error status other
    public static final int SERVICE_UNKNOW_ERROR_CODE = 10000;
    
    // framework error
    public static final int FRAMEWORK_DEFAULT_ERROR_CODE = 20001;
    public static final int FRAMEWORK_INIT_ERROR_CODE = 20004;

    
    // biz exception
    public static final int BIZ_DEFAULT_ERROR_CODE = 30001;
    
    
    /**
     * service error start
     **/

    public static final FinixErrorMsg SERVICE_DEFAULT_ERROR = new FinixErrorMsg(503, SERVICE_DEFAULT_ERROR_CODE, "service error");
    public static final FinixErrorMsg SERVICE_TIMEOUT = new FinixErrorMsg(503, SERVICE_TIMEOUT_ERROR_CODE, "service request timeout");
    public static final FinixErrorMsg SERVICE_UNFOUND = new FinixErrorMsg(404, SERVICE_UNFOUND_ERROR_CODE, "service unfound");

    /**
     * framework error start
     **/
    public static final FinixErrorMsg FRAMEWORK_DEFAULT_ERROR = new FinixErrorMsg(503, FRAMEWORK_DEFAULT_ERROR_CODE,"framework default error");
    
    public static final FinixErrorMsg FRAMEWORK_INIT_ERROR = new FinixErrorMsg(500, FRAMEWORK_INIT_ERROR_CODE, "framework init error");

    
    /**
     * biz error start
     **/
    public static final FinixErrorMsg BIZ_DEFAULT_EXCEPTION = new FinixErrorMsg(503, BIZ_DEFAULT_ERROR_CODE, "provider error");
    
}
