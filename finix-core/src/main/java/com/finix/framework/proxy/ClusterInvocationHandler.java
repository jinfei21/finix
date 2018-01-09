package com.finix.framework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import com.finix.framework.cluster.ClusterCaller;
import com.finix.framework.common.URLParamType;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.rpc.DefaultRequest;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.RpcContext;
import com.finix.framework.util.ExceptionUtil;
import com.finix.framework.util.FinixFrameworkUtil;
import com.finix.framework.util.IdGeneratorUtil;
import com.finix.framework.util.ReflectUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ClusterInvocationHandler<T> implements InvocationHandler  {
	
    private Class<?> interfaceClass;
	
	private ClusterCaller clusterCaller;
	
	public ClusterInvocationHandler(ClusterCaller clusterCaller) throws ClassNotFoundException{
		this.clusterCaller = clusterCaller;
		this.interfaceClass = ReflectUtil.forName(clusterCaller.getInterfaceClass());
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		if(isLocalMethod(method)){
			
			if("toString".equals(method.getName())){
				return clustersToString();
			}
			
			if("equals".equals(method.getName())){
				return proxyEquals(args[0]);
			}
			
			throw new FinixServiceException("can not invoke local method:" + method.getName());
		}
		
		DefaultRequest  request = new DefaultRequest();
		request.setRequestId(IdGeneratorUtil.getRequestId());
		request.setInterfaceName(this.interfaceClass.getName());
		request.setMethodName(method.getName());
		request.setParamDesc(ReflectUtil.getMethodParamDesc(method));
		request.setArguments(args);
		request.setReturnType(method.getReturnType().getName());
		
		RpcContext rpcContext = RpcContext.getContext();
		rpcContext.putAttribute(URLParamType.socketTimeout.getName(), clusterCaller.getReferUrl().getIntParameter(URLParamType.socketTimeout.getName(), URLParamType.socketTimeout.getIntValue()));
		rpcContext.putAttribute(URLParamType.requestConnectTimeout.getName(), clusterCaller.getReferUrl().getIntParameter(URLParamType.requestConnectTimeout.getName(), URLParamType.requestConnectTimeout.getIntValue()));
		rpcContext.putAttribute(URLParamType.connectTimeout.getName(), clusterCaller.getReferUrl().getIntParameter(URLParamType.connectTimeout.getName(), URLParamType.connectTimeout.getIntValue()));

		request.setAttachment(URLParamType.version.getName(), clusterCaller.getReferUrl().getVersion());
		
		try{
			Response response = clusterCaller.call(request);
			return response.getValue();
		}catch(RuntimeException e){
			if(ExceptionUtil.isBizException(e)){
				Throwable t = e.getCause();
                // 只抛出Exception，防止抛出远程的Error
                if (t != null && t instanceof Exception) {
                    throw t;
                } else {
                    String msg = t == null
                            ? "Biz exception cause is null. origin error message : " + e.getMessage()
                            : ("Biz exception cause is throwable error:" + t.getClass() + ", error message:" + t.getMessage());
                    throw new FinixServiceException(msg);
                }
            } else {
                log.error("InvocationHandler invoke Error: uri={}, request={}",
                        clusterCaller.getReferUrl().getUri(), FinixFrameworkUtil.toString(request), e);
                throw e;
            }
		}
		
	}

    /**
     * tostring,equals,hashCode,finalize等接口未声明的方法不进行远程调用
     *
     * @param method
     * @return
     */
    private boolean isLocalMethod(Method method) {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
            	interfaceClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return false;
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }
    
    private boolean proxyEquals(Object o) {
        if (o == null || this.clusterCaller == null) {
            return false;
        }
        if (o instanceof List) {
            return this.clusterCaller == o;
        } else {
            return o.equals(this.clusterCaller);
        }
    }
    
    private String clustersToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{protocol:").append(clusterCaller.getReferUrl().getProtocol());
        List<Refer> refers = clusterCaller.getRefers();
        if (refers != null) {
            for (Refer refer : refers) {
                sb.append("[").append(refer.getServiceUrl().getUri()).append(", available:").append(refer.isAvailable()).append("]");
            }
        }
        sb.append("}");
        return sb.toString();
    }
	
}
