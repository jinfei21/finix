package com.finix.framework.rpc;

import java.lang.reflect.Method;

import com.finix.framework.common.URLParamType;
import com.finix.framework.exception.ErrorMsgConstants;
import com.finix.framework.exception.FinixBizException;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.util.ExceptionUtil;
import com.finix.framework.util.ReflectUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultProvider<T> extends AbstractProvider<T> {

	public DefaultProvider(Class<T> interfaceClass, T serviceInstance, URL serviceUrl) {
		super(interfaceClass, serviceInstance, serviceUrl);
	}

	@Override
	protected Response invoke(Request request) {

		DefaultResponse response = new DefaultResponse();

		Method method = lookupMethod(request.getMethodName(), request.getParamDesc());

		if (method == null) {
			FinixServiceException exception = new FinixServiceException("Service method not exist: " 
			+ request.getInterfaceName() + "#"
			+ ReflectUtil.getMethodDesc(request.getMethodName(), request.getParamDesc()),
			ErrorMsgConstants.SERVICE_UNFOUND);
			response.setException(exception);
			
			return response;
		}
		
		try{
			Object value = method.invoke(this.serviceInstance, request.getArguments());
			response.setValue(value);
		}catch(Exception e){
			if(e.getCause() != null){
				response.setException(new FinixBizException("provider call process error", e.getCause()));
			}else{
				response.setException(new FinixBizException("provider call process error", e));
			}
			//服务发生错误时，显示详细日志
			log.error("Exception caught when during method invocation. request:" + request.toString(), e);
		}catch(Throwable t){
			// 如果服务发生Error，将Error转化为Exception，防止拖垮调用方
			if(t.getCause() != null){
				response.setException(new FinixServiceException("provider has encountered a fatal error!", t.getCause()));
			}else{
				response.setException(new FinixServiceException("provider has encountered a fatal error!", t));
			}
            //对于Throwable,也记录日志
            log.error("Exception caught when during method invocation. request:" + request.toString(), t);
		}
		
        if (response.getException() != null) {
            //是否传输业务异常栈
            boolean transExceptionStack = this.serviceUrl.getBooleanParameter(URLParamType.transExceptionStack.getName(), URLParamType.transExceptionStack.getBooleanValue());
            if (!transExceptionStack) {//不传输业务异常栈
                ExceptionUtil.setMockStackTrace(response.getException().getCause());
            }
        }

		return response;
	}

}
