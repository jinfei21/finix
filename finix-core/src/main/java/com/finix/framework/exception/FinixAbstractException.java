package com.finix.framework.exception;

import com.finix.framework.rpc.RpcContext;

public class FinixAbstractException extends RuntimeException {
    private static final long serialVersionUID = -8742311167276890503L;

	    protected FinixErrorMsg errorMsg = ErrorMsgConstants.FRAMEWORK_DEFAULT_ERROR;
	    protected String errorMessage = null;

	    public FinixAbstractException() {
	        super();
	    }

	    public FinixAbstractException(FinixErrorMsg motanErrorMsg) {
	        super();
	        this.errorMsg = motanErrorMsg;
	    }

	    public FinixAbstractException(String message) {
	        super(message);
	        this.errorMessage = message;
	    }

	    public FinixAbstractException(String message, FinixErrorMsg motanErrorMsg) {
	        super(message);
	        this.errorMsg = motanErrorMsg;
	        this.errorMessage = message;
	    }

	    public FinixAbstractException(String message, Throwable cause) {
	        super(message, cause);
	        this.errorMessage = message;
	    }

	    public FinixAbstractException(String message, Throwable cause, FinixErrorMsg motanErrorMsg) {
	        super(message, cause);
	        this.errorMsg = motanErrorMsg;
	        this.errorMessage = message;
	    }

	    public FinixAbstractException(Throwable cause) {
	        super(cause);
	    }

	    public FinixAbstractException(Throwable cause, FinixErrorMsg motanErrorMsg) {
	        super(cause);
	        this.errorMsg = motanErrorMsg;
	    }

	    @Override
	    public String getMessage() {
	        String message = getOriginMessage();

	        return "error_message: " + message + ", status: " + errorMsg.getStatus() + ", error_code: " + errorMsg.getErrorCode()
	                + ",r=" + RpcContext.getContext().getRequestId();
	    }

	    public String getOriginMessage(){
	        if (errorMsg == null) {
	            return super.getMessage();
	        }

	        String message;

	        if (errorMessage != null && !"".equals(errorMessage)) {
	            message = errorMessage;
	        } else {
	            message = errorMsg.getMessage();
	        }
	        return message;
	    }

	    public int getStatus() {
	        return errorMsg != null ? errorMsg.getStatus() : 0;
	    }

	    public int getErrorCode() {
	        return errorMsg != null ? errorMsg.getErrorCode() : 0;
	    }

	    public FinixErrorMsg getMotanErrorMsg() {
	        return errorMsg;
	    }
	}
