package com.finix.framework.exception;
public class FinixBizException extends FinixAbstractException {
    private static final long serialVersionUID = -3491276058323309898L;

    public FinixBizException() {
        super(ErrorMsgConstants.BIZ_DEFAULT_EXCEPTION);
    }

    public FinixBizException(FinixErrorMsg finixErrorMsg) {
        super(finixErrorMsg);
    }

    public FinixBizException(String message) {
        super(message, ErrorMsgConstants.BIZ_DEFAULT_EXCEPTION);
    }

    public FinixBizException(String message, FinixErrorMsg finixErrorMsg) {
        super(message, finixErrorMsg);
    }

    public FinixBizException(String message, Throwable cause) {
        super(message, cause, ErrorMsgConstants.BIZ_DEFAULT_EXCEPTION);
    }

    public FinixBizException(String message, Throwable cause, FinixErrorMsg finixErrorMsg) {
        super(message, cause, finixErrorMsg);
    }

    public FinixBizException(Throwable cause) {
        super(cause, ErrorMsgConstants.BIZ_DEFAULT_EXCEPTION);
    }

    public FinixBizException(Throwable cause, FinixErrorMsg finixErrorMsg) {
        super(cause, finixErrorMsg);
    }
}
