package com.finix.framework.transport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import com.finix.framework.common.ClientConfig;
import com.finix.framework.common.Constants;
import com.finix.framework.common.URLParamType;
import com.finix.framework.exception.ErrorMsgConstants;
import com.finix.framework.exception.FinixAbstractException;
import com.finix.framework.exception.FinixErrorMsg;
import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.rpc.DefaultResponse;
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
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class FinixApacheHttpClientFactory extends HttpClientFactory{

	private ClientConfig clientConfig;
	
    private HttpClientConnectionManager connectionManager;

    private SerializationFactory serializationFactory;
	
	public FinixApacheHttpClientFactory(ClientConfig clientConfig){
		this.clientConfig = clientConfig;
		this.init();
	}
	
	@Override
	public void init() {
		if(this.connectionManager == null){
			this.connectionManager = buildConnectionManager();
		}
		
		if(this.serializationFactory == null){
			this.serializationFactory = new ProtobufSerializationFactory();
		}
	}

	@Override
	public void destroy() {
		this.connectionManager.shutdown();
	}

	@Override
	public HttpClient createHttpClient(URL serviceUrl) {

		return new HttpClient(){

			@Override
			public Response sendRequest(Request request) {
				long start = System.currentTimeMillis();
				DefaultResponse response = null;
				
				try{
					response = sendHttpRequest(serviceUrl,request);
				}catch(Exception e){
					response = new DefaultResponse(request.getRequestId());
					response.setException(e);
				}finally{
					if(response != null){
						response.setProcessTime(System.currentTimeMillis() - start);
					}
				}
				return response;
			}
			
		};
		
	}
	
	protected DefaultResponse sendHttpRequest(URL serviceUrl,Request request){
		request.getAttachments().putAll(RpcContext.getContext().getRpcAttachments());
		
		DefaultResponse response = new DefaultResponse(request.getRequestId());
		
		CloseableHttpResponse httpResponse = null;
		
		try{
			CloseableHttpClient httpClient = this.buildHttpClient();
			HttpPost httpPost = buildPost(serviceUrl,request);
			request.setAttachment(URLParamType.requestPayloadSize.getName(), String.valueOf(httpPost.getEntity().getContentLength()));
			try{
				httpResponse = httpClient.execute(httpPost);
			}catch(IOException e){
				throw new FinixServiceException("Error send http post, requestId=" + request.getRequestId(), e);
			}
			
			setResponseHeaders(httpResponse,response);
			
			setStatus(httpResponse,response);
			
			setResult(httpResponse, request, response);
			
		}catch(Exception e){
			response.setException(e);
		}finally{
			try{
                if (httpResponse != null) {
                    httpResponse.close();
                }
			}catch(IOException e){
				  log.warn("Error close httpResponse, requestId={}", request.getRequestId(), e);
			}
		}
        return response;
	}
	
    protected void setResponseHeaders(CloseableHttpResponse httpResponse, Response response) {
        Header[] headers = httpResponse.getAllHeaders();
        for (Header header : headers) {
            response.setAttachment(header.getName(), header.getValue());
        }
    }
    
    protected void setStatus(CloseableHttpResponse httpResponse, Response response) {
        StatusLine statusLine = httpResponse.getStatusLine();
        response.setAttachment(URLParamType.httpVersion.name(), statusLine.getProtocolVersion().toString());
        response.setAttachment(URLParamType.httpStatusCode.name(), String.valueOf(statusLine.getStatusCode()));
        response.setAttachment(URLParamType.httpReasonPhrase.name(), statusLine.getReasonPhrase());
    }
	
    protected Object getResponseValue(Request request, byte[] content) {
        Class<?> returnType = request.getReturnType();
        Serialization serialization = this.buildSerialization(request);
        try {
            return serialization.deserialize(content, returnType);
        } catch (IOException e) {
            throw new FinixServiceException("Deserialize response content error, requestId=" + request.getRequestId(), e);
        }
    }
    
    protected Exception getResponseException(Request request, byte[] content) {
        //TODO 与服务端一致,暂时使用java的Object序列化
        if (content == null || content.length == 0) {
            return null;
        }
        
        try{
        	return new RuntimeException(new String(content));
        }catch(Exception e) {
        	 return new FinixServiceException("Read response exception content error, requestId=" + request.getRequestId(), e);
        }
//        try {
//            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(content));
//            Object obj = in.readObject();
//            if (obj instanceof Exception) {
//                return (Exception) obj;
//            } else {
//                return new FinixServiceException("Read response exception content error, requestId=" + request.getRequestId());
//            }
//        } catch (Exception e) {
//            return new FinixServiceException("Read response exception content error, requestId=" + request.getRequestId(), e);
//        }
    }
    
    protected void setResult(CloseableHttpResponse httpResponse, Request request, DefaultResponse response) {
        HttpEntity entity = httpResponse.getEntity();
        byte[] content;
        try {
            content = IOUtils.toByteArray(entity.getContent());
            response.setAttachment(URLParamType.responsePayloadSize.getName(), String.valueOf(content.length));
        } catch (IOException e) {
            throw new FinixServiceException("Error read response content, requestId=" + response.getRequestId(), e);
        }
        
        //  根据http状态码做不同的处理
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            response.setValue(getResponseValue(request, content));
            response.setCode(ErrorMsgConstants.SUCCESS);
        } else if (statusCode >= 300 && statusCode < 400) {
            response.setException(new FinixServiceException(new FinixErrorMsg(statusCode,
            		ErrorMsgConstants.SERVICE_REDIRECT_ERROR_CODE, "Http request redirect error.")));
            response.setCode(ErrorMsgConstants.SERVICE_REDIRECT_ERROR_CODE);
        } else if (statusCode == 404) {
            response.setException(new FinixServiceException(ErrorMsgConstants.SERVICE_UNFOUND));
            response.setCode(ErrorMsgConstants.SERVICE_UNFOUND_ERROR_CODE);
        } else if (statusCode >= 400 && statusCode < 600) {//4xx,5xx处理逻辑是一样的
            Exception e = getResponseException(request, content);
            int code = ErrorMsgConstants.SERVICE_DEFAULT_ERROR_CODE;
            if (e == null) {
                e = new FinixServiceException(new FinixErrorMsg(statusCode,
                		ErrorMsgConstants.SERVICE_UNKNOW_ERROR_CODE, "Unknow error, status code is " + statusCode));
            } else if (ExceptionUtil.isFinixException(e)) {
                code = ((FinixAbstractException) e).getErrorCode();
                code = code == 0 ? ErrorMsgConstants.SERVICE_DEFAULT_ERROR_CODE : code;
            }
            response.setException(e);
            response.setCode(code);
        } else {
            response.setException(new FinixServiceException(new FinixErrorMsg(statusCode,
            		ErrorMsgConstants.SERVICE_UNKNOW_ERROR_CODE, "Unknow error.")));
            response.setCode(ErrorMsgConstants.SERVICE_UNKNOW_ERROR_CODE);
        }
    }
    protected HttpPost buildPost(URL serviceUrl, Request request) {
        URI uri = buildUri(request, serviceUrl);
        HttpPost post = buildPost(uri.toString());
        for (Header header : buildHeaders(request)) {
            post.addHeader(header);
        }
        post.setEntity(buildHttpEntity(request));
        return post;
    }
    
    protected HttpEntity buildHttpEntity(Request request) {
    
        //这里因为是finix协议，请求参数只能是一个protobuf Message
    	if(request.getArguments().length == 1){
    		try{
    			Serialization serialization = this.buildSerialization(request);
    			byte[] data = serialization.serialize(request.getArguments()[0]);
    			EntityBuilder entityBuilder = EntityBuilder.create().setBinary(data);    			
    			return entityBuilder.build();
    		}catch(IOException e){
    			throw new FinixFrameworkException("Can not serialize request Argument: " + request.getArguments()[0], e);
    		}    		
    	}
    	
    	if(request.getArguments().length == 0){
            return EntityBuilder.create().build();
    	}
    	
        throw new FinixServiceException(String.format("Interface method %s#%s  parameter count must less or equal 1, but has %s :",
                request.getInterfaceName(),
                ReflectUtil.getMethodSignature(request.getMethodName(), request.getParameterTypes()),
                request.getArguments().length));
    }
    
    protected Serialization buildSerialization(Request request) {
        String serializationParam = getSerializationParam(request);
        return this.getSerializationFactory().getInstance(serializationParam);
    }
    
    protected String getSerializationParam(Request request) {
        return MapUtils.getString(request.getAttachments(),
                URLParamType.serialization.getName(), URLParamType.serialization.getValue());
    }
    
    protected List<Header> buildHeaders(Request request){
    	List<Header> headers = Lists.newArrayList();
    	Map<String,String> attachments = request.getAttachments();
    	
    	for(Entry<String,String> entry:attachments.entrySet()){
    		Header header = new BasicHeader(entry.getKey(), entry.getValue());
    		headers.add(header);
    	}
    	
    	headers.add(new BasicHeader(URLParamType.requestId.getName(),String.valueOf(request.getRequestId())));;
    	
    	if(request.getParameterTypes() != null){
    		headers.add(new BasicHeader(URLParamType.parameterTypes.getName(),StringUtils.join(request.getParameterTypes())));
    	}
    	
    	if(request.getReturnType() != null){
    		headers.add(new BasicHeader(URLParamType.returnType.getName(),request.getReturnType().getName()));
    	}
    	
    	return headers;
    }
	
    protected HttpPost buildPost(String url) {
        HttpPost post = new HttpPost(url);
        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectTimeout(this.getConnectTimeout())
                .setSocketTimeout(this.getSocketTimeout())
                .setConnectionRequestTimeout(this.getRequestConnectTimeout());
        post.setConfig(builder.build());
        return post;
    }
    
    protected URI buildUri(Request request, URL serviceUrl) {
        try {
            String basePath = serviceUrl.getParameter(URLParamType.basePath.name(), URLParamType.basePath.getValue());
            String path = Constants.PATH_SEPARATOR + StringUtils.removeStart(basePath, Constants.PATH_SEPARATOR)
                    + Constants.PATH_SEPARATOR + serviceUrl.getPath()
                    + Constants.PATH_SEPARATOR + request.getMethodName();
            URIBuilder builder = new URIBuilder();
            return builder.setScheme(MapUtils.getString(serviceUrl.getParameters(), URLParamType.httpSchema.getName(), URLParamType.httpSchema.getValue()))
                    .setHost(serviceUrl.getHost())
                    .setPort(serviceUrl.getPort())
                    .setPath(path)
                    .build();
        } catch (Exception e) {
            throw new FinixFrameworkException("build request uri error.", e);
        }
    }
    
    public int getConnectTimeout(){
    	Object connectTimeout = RpcContext.getContext().getAttribute(URLParamType.connectTimeout.getName());
    	if(connectTimeout != null){
    		return NumberUtil.parseInt(String.valueOf(connectTimeout), this.clientConfig.getConnectTimeout());
    	}
    	return this.clientConfig.getConnectTimeout();
    }
    
    public int getSocketTimeout(){
    	Object socketTimeout = RpcContext.getContext().getAttribute(URLParamType.socketTimeout.getName());
    	if(socketTimeout != null){
    		return NumberUtil.parseInt(String.valueOf(socketTimeout), this.clientConfig.getSocketTimeout());
    	}
    	return this.clientConfig.getSocketTimeout();
    }
    
    public int getRequestConnectTimeout(){
    	Object requestConnectTimeout = RpcContext.getContext().getAttribute(URLParamType.requestConnectTimeout.getName());

    	if(requestConnectTimeout != null){
    		return NumberUtil.parseInt(String.valueOf(requestConnectTimeout), this.clientConfig.getRequestConnectTimeout());
    	}
    	return this.clientConfig.getRequestConnectTimeout();
    }
	
	protected CloseableHttpClient buildHttpClient(){
		RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(this.clientConfig.getRetryCount(), this.clientConfig.isRequestSentRetryEnabled());
		
		return HttpClients.custom()
				.disableContentCompression()
				.setConnectionManager(this.connectionManager)
				.setDefaultRequestConfig(config)
				.setRetryHandler(retryHandler)
				.disableCookieManagement()
				.build();
	}
	
    protected HttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(clientConfig.getPoolMaxTotal());
        connectionManager.setDefaultMaxPerRoute(clientConfig.getPoolMaxPreRoute());
        return connectionManager;
    }
	
}
