package com.finix.framework.rpc;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.finix.framework.util.ReflectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractProvider<T> implements Provider{

	protected String interfaceClass;
	
	@Getter
	protected Map<String,Method> methodMap;

	@Getter
	protected T serviceInstance;
	
	@Getter
	@Setter
	protected URL serviceUrl;

	public AbstractProvider(Class<T> interfaceClass,T serviceInstance,URL serviceUrl){
		this.interfaceClass = interfaceClass.getName();
		this.serviceInstance = serviceInstance;
		this.serviceUrl = serviceUrl;
		initMethodMap(interfaceClass);
	}
	
    @Override
    public Method lookupMethod(String methodName, String[] parameterTypes) {
    	
    	Method method = this.getMethodMap().get(methodName);
    	
    	if(method == null){
            String key = ReflectUtil.getMethodSignature(methodName, parameterTypes);
            return this.getMethodMap().get(key);
    	}
    	
    	return method;
    }
	
    /**
     * 初始化接口的方法
     * <p>
     * 假设方法：
     * String test(String)
     * String test1(String)
     * String test1(String,int)
     * <p>
     * 结果：
     * test(String)方法将生成两个key： test，test(String)，因为test方法没有重载，直接用方法名方便使用
     * test1(String)方法将生成一个key：test1(String)
     * test1(String,int)方法将生成一个key：test1(String,int)
     * <p>
     *
     * @param clazz
     */
    private void initMethodMap(Class<T> clazz){
    	this.methodMap = Maps.newHashMap();
    	Method[] methods = clazz.getMethods();
    	Map<String,List<Method>> nameMethodMap = Maps.newHashMap();
    	
    	for(Method method:methods){
    		nameMethodMap.put(method.getName(), Lists.newArrayList());
    		List<Method> nameMethods = nameMethodMap.get(method.getName());
    		nameMethods.add(method);
    	}
    	
    	for(String methodName:nameMethodMap.keySet()){
    		List<Method> nameMethods = nameMethodMap.get(methodName);
    		if(nameMethods.size() == 1){
    			Method method = nameMethods.get(0);
    			this.methodMap.put(methodName, method);
    		}
    		for(Method method:nameMethods){
    			this.methodMap.put(ReflectUtil.getMethodSignature(method), method);
    		}
    	}
    }
    
    
    @Override
    public void destroy() {

    }

    @Override
    public void init() {

    }
    
    public T getImpl() {
        return this.serviceInstance;
    }

    @Override
    public String getInterface() {
        return this.interfaceClass;
    }
    
    @Override
    public Response call(Request request) {
        return invoke(request);
    }

    protected abstract Response invoke(Request request);
}
