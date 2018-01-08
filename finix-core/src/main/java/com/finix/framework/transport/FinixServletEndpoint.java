package com.finix.framework.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.finix.framework.common.Constants;
import com.finix.framework.common.URLParamType;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.rpc.DefaultRequest;
import com.finix.framework.rpc.DefaultResponse;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.RpcContext;
import com.finix.framework.rpc.URL;
import com.finix.framework.serialize.ProtobufSerializationFactory;
import com.finix.framework.serialize.Serialization;
import com.finix.framework.serialize.SerializationFactory;
import com.finix.framework.util.ExceptionUtil;
import com.finix.framework.util.NumberUtil;
import com.finix.framework.util.ReflectUtil;
import com.google.common.collect.Maps;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FinixServletEndpoint extends AbstractServletEndpoint{
	
	private ConcurrentMap<String, Class<?>> returnTypeClazzCache = Maps.newConcurrentMap();
	
    @Setter
    protected SerializationFactory serializationFactory;

	public FinixServletEndpoint(URL baseUrl) {
		super(baseUrl);
	}
	
	@Override
	public void init()throws ServletException {
        super.init();
        if (this.serializationFactory == null)
            this.serializationFactory = new ProtobufSerializationFactory();
    }
	
	
	@Override
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		String key = getProviderKey(getInterfaceName(httpRequest), getVersion(httpRequest));
		Provider provider  = this.providers.get(key);
		if(provider == null){
			throw new FinixServiceException("Can not find provider by key: " + key);
		}
		
		Request request = convert(httpRequest,provider);
		
        Response response;
        try {
            response = provider.call(request);
        }catch (Exception e) {
            DefaultResponse errorResponse = new DefaultResponse();
            errorResponse.setRequestId(request.getRequestId());
            errorResponse.setException(e);
            response = errorResponse;
            //TODO 详细日志
            log.error("Request error.",e);
        }
        writeResponse(request, response, httpRequest, httpResponse);
	}
	
	protected Request convert(HttpServletRequest httpRequest,Provider provider){
		DefaultRequest request = new DefaultRequest();
		
		request.setRequestId(NumberUtil.parseLong(httpRequest.getHeader(URLParamType.requestId.name()), 0L));
		request.setInterfaceName(getInterfaceName(httpRequest));
		request.setMethodName(getMethodName(httpRequest));
		request.setParameterTypes(getParameterTypes(httpRequest));
		request.setReturnType(getReturnType(httpRequest));
		request.setAttachments(getAttachments(httpRequest));		
		request.setArguments(getRequestArguments(httpRequest, request, provider));
		RpcContext.getContext().setRequest(request);
		
		return request;
	}
	
	protected Object[] getRequestArguments(HttpServletRequest httpRequest, Request request, Provider provider) {
		Method method = provider.lookupMethod(request.getMethodName(), request.getParameterTypes());
		if (method == null) {
			throw new FinixServiceException(
					String.format("Can not find method %s#%s", request.getInterfaceName(), request.getMethodName()));
		}
		if (method.getParameterCount() == 1) {
			Serialization serialization = this.getSerialization(httpRequest);
			try {
				byte[] data = IOUtils.toByteArray(httpRequest.getInputStream());
				Object argument = serialization.deserialize(data, method.getParameterTypes()[0]);
				return new Object[] { argument };
			} catch (IOException e) {
				throw new FinixServiceException("Deserialize request parameter error.", e);
			}
		} else if (method.getParameterCount() == 0) {
			return new Object[0];
		} else {
			throw new FinixServiceException(
					String.format("Interface method %s#%s parameter count must less or equal 1.",
							request.getInterfaceName(), method.getName()));
		}
	}
	
    protected Serialization getSerialization(HttpServletRequest httpRequest) {
        return this.serializationFactory.getInstance(httpRequest.getHeader(URLParamType.serialization.getName()));
    }
    
    protected Map<String, String> getAttachments(HttpServletRequest httpRequest) {
        Map<String, String> attachments = new HashMap<>();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headName = headerNames.nextElement();
            String headerValue = httpRequest.getHeader(headName);
            attachments.put(headName, headerValue);
        }
        attachments.put(URLParamType.clientHost.getName(), httpRequest.getRemoteAddr());
        return attachments;
    }
    
    protected Class<?> getReturnType(HttpServletRequest httpRequest) {
        String returnType = httpRequest.getHeader(URLParamType.returnType.name());
        if (returnType != null) {
            try {
                return ReflectUtil.forName(returnType);
            } catch (ClassNotFoundException e) {
                throw new FinixServiceException("Can not find returnType " + returnType, e);
            }
        }
        return null;
    }
    
    protected String[] getParameterTypes(HttpServletRequest httpRequest) {
        String parameterTypes = httpRequest.getHeader(URLParamType.parameterTypes.name());
        if (parameterTypes != null) {
            return parameterTypes.split(Constants.SEPERATOR_ARRAY);
        }
        return null;
    }
	
    protected String getInterfaceName(HttpServletRequest httpRequest) {
        String path = StringUtils.substringBeforeLast(httpRequest.getPathInfo(), Constants.PATH_SEPARATOR);
        return StringUtils.substringAfterLast(path, Constants.PATH_SEPARATOR);
    }
    
    protected String getMethodName(HttpServletRequest httpRequest) {
        return StringUtils.substringAfterLast(httpRequest.getPathInfo(), Constants.PATH_SEPARATOR);
    }
    
    protected String getVersion(HttpServletRequest httpRequest) {
        return httpRequest.getHeader(URLParamType.version.name());
    }
    
    
    protected void writeResponse(Request request, Response response, HttpServletRequest httpRequest, HttpServletResponse httpServletResponse) {
        if (response.getException() != null) {
            httpServletResponse.setStatus(Constants.HTTP_EXPECTATION_FAILED);
//            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
//                 OutputStream out = httpServletResponse.getOutputStream()) {
//                objectOutputStream.writeObject(response.getException());
//                objectOutputStream.flush();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                byte[] data = baos.toByteArray();
//                out.write(data);
//                out.flush();
//            } catch (IOException e) {
//                log.error("write response error.request: {}", request, e);
//            }
            try (OutputStream out = httpServletResponse.getOutputStream()) {
            	String stackTraceStr = ExceptionUtil.getStackTraceString(response.getException());
	            out.write(stackTraceStr.getBytes());
	            out.flush();
            }catch (IOException e) {
              log.error("write response error.request: {}", request, e);
            }          
        } else {
            httpServletResponse.setStatus(Constants.HTTP_OK);
            try (OutputStream out = httpServletResponse.getOutputStream()) {
                Serialization serialization = this.getSerialization(httpRequest);
                byte[] data = serialization.serialize(response.getValue());
                out.write(data);
                out.flush();
            } catch (IOException e) {
                log.error("write response error.request: {}", request, e);
            }
        }
    }
}
