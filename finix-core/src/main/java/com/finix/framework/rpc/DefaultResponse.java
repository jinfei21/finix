package com.finix.framework.rpc;

import java.io.Serializable;
import java.util.Map;

import com.finix.framework.exception.FinixServiceException;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultResponse implements Response,Serializable{

	private static final long serialVersionUID = -309865843521982571L;

	private int code = -1;
	private Object value;
	private Exception exception;
	private long requestId;
	private long processTime;
	private int timeout;
	
	private Map<String,String> attachments = Maps.newHashMap();
	
	public DefaultResponse(){
		
	}
	
	public DefaultResponse(long requestId){
		this.requestId = requestId;
	}
	
    public DefaultResponse(Response response) {
        this.value = response.getValue();
        this.exception = response.getException();
        this.requestId = response.getRequestId();
        this.processTime = response.getProcessTime();
        this.timeout = response.getTimeout();
    }

    public DefaultResponse(Object value) {
        this.value = value;
    }

    public DefaultResponse(Object value, long requestId) {
        this.value = value;
    }

	
	@Override
	public Object getValue() {
		if(exception != null){
            throw (exception instanceof RuntimeException) ? (RuntimeException) exception : new FinixServiceException(
                    exception.getMessage(), exception);
		}
		return value;
	}

    @Override
    public void setAttachment(String key, String value) {
        if (this.attachments == null) {
            this.attachments = Maps.newHashMap();
        }
        this.attachments.put(key, value);
    }

}
