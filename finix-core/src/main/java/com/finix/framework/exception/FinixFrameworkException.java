package com.finix.framework.exception;
public class FinixFrameworkException extends FinixAbstractException {

    public FinixFrameworkException() {
        super(ErrorMsgConstants.FRAMEWORK_DEFAULT_ERROR);
    }

    public FinixFrameworkException(FinixErrorMsg finixErrorMsg) {
        super(finixErrorMsg);
    }

    public FinixFrameworkException(String message) {
        super(message, ErrorMsgConstants.FRAMEWORK_DEFAULT_ERROR);
    }

    public FinixFrameworkException(String message, FinixErrorMsg finixErrorMsg) {
        super(message, finixErrorMsg);
    }

    public FinixFrameworkException(String message, Throwable cause) {
        super(message, cause, ErrorMsgConstants.FRAMEWORK_DEFAULT_ERROR);
    }

    public FinixFrameworkException(String message, Throwable cause, FinixErrorMsg finixErrorMsg) {
        super(message, cause, finixErrorMsg);
    }

    public FinixFrameworkException(Throwable cause) {
        super(cause, ErrorMsgConstants.FRAMEWORK_DEFAULT_ERROR);
    }

    public FinixFrameworkException(Throwable cause, FinixErrorMsg finixErrorMsg) {
        super(cause, finixErrorMsg);
    }

}