package com.finix.framework.exception;
public class FinixServiceException extends FinixAbstractException {
    private static final long serialVersionUID = -3491276058323309898L;

    public FinixServiceException() {
        super(ErrorMsgConstants.SERVICE_DEFAULT_ERROR);
    }

    public FinixServiceException(FinixErrorMsg motanErrorMsg) {
        super(motanErrorMsg);
    }

    public FinixServiceException(String message) {
        super(message, ErrorMsgConstants.SERVICE_DEFAULT_ERROR);
    }

    public FinixServiceException(String message, FinixErrorMsg motanErrorMsg) {
        super(message, motanErrorMsg);
    }

    public FinixServiceException(String message, Throwable cause) {
        super(message, cause, ErrorMsgConstants.SERVICE_DEFAULT_ERROR);
    }

    public FinixServiceException(String message, Throwable cause, FinixErrorMsg motanErrorMsg) {
        super(message, cause, motanErrorMsg);
    }

    public FinixServiceException(Throwable cause) {
        super(cause, ErrorMsgConstants.SERVICE_DEFAULT_ERROR);
    }

    public FinixServiceException(Throwable cause, FinixErrorMsg motanErrorMsg) {
        super(cause, motanErrorMsg);
    }
}