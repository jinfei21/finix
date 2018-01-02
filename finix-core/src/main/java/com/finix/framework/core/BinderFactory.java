package com.finix.framework.core;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;

import com.finix.framework.common.Constants;
import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.util.SpiBinderScannerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;

@Data
public class BinderFactory {

	private static BinderFactory instance;
	private ConcurrentMap<Class<?>,Map<String,Binder<?>>> binderMap = Maps.newConcurrentMap();
	
	
	public static BinderFactory getInstance(){
			if(instance == null){
				synchronized (BinderFactory.class) {
					if(instance == null){
						instance = new BinderFactory();
					}
				}
			}
			return instance;
	}
	
	private BinderFactory(){
		String pkg = this.getClass().getPackage().getName();
		pkg = StringUtils.substringBeforeLast(pkg, ".");
		doScan(Lists.newArrayList(pkg));
	}
	
	public void doScan(List<String> pakages){
		Map<Class<?>,List<Class<?>>> binderMap = SpiBinderScannerUtil.scanBinders(pakages);
		
		for(Entry<Class<?>,List<Class<?>>> entryClazz:binderMap.entrySet()){
			for(Class binderClazz:entryClazz.getValue()){
				Binder<?> binder = createBinderDefine(entryClazz.getKey(), binderClazz);
                registerBinder(binder);
			}
		}
	}
	
	public <T> void registerBinder(Binder<T> binder){
		Binder<T> newBinder = new Binder<>();
		newBinder.setInterfaceClass(binder.getInterfaceClass());
		newBinder.setName(StringUtils.isBlank(binder.getName())?Constants.DEFAULT_BINDER_NAME:binder.getName());
		newBinder.setBinderClass(binder.getBinderClass());
		newBinder.setScope(binder.getScope() == null?Scope.SINGLETON:binder.getScope());
		newBinder.setOrder(binder.getOrder());
		
		Map<String,Binder<?>> map = binderMap.get(binder.getInterfaceClass());
		
		if(map == null){
			map = Maps.newConcurrentMap();
			Map old = binderMap.putIfAbsent(binder.getInterfaceClass(), map);
			if(old == null){
				map = binderMap.get(binder.getInterfaceClass());
			}
		}
		
		if(map.containsKey(newBinder.getName())){
            Binder<?> cached = map.get(newBinder.getName());
            if (!cached.equals(newBinder)) {
                throw new FinixFrameworkException(String.format("Interface %s binder name=%s aready exist with class %s,can not bind class %s.", newBinder.getInterfaceClass(), newBinder.getName(), newBinder.getBinderClass(), cached.getBinderClass()));
            }
            return;
		}
		map.put(newBinder.getName(), newBinder);
	}
	
    public static <T> Binder<T> createBinderDefine(Class<T> interfaceClass, Class<? extends T> binderClass) {
        Spi spi = interfaceClass.getAnnotation(Spi.class);
        SpiBinder prpcBinder = binderClass.getAnnotation(SpiBinder.class);

        Binder<T> binderDefine = new Binder<>();
        binderDefine.setInterfaceClass(interfaceClass);
        binderDefine.setBinderClass(binderClass);
        binderDefine.setScope(spi == null ? Scope.SINGLETON : spi.scope());
        binderDefine.setName(prpcBinder == null ? Constants.DEFAULT_BINDER_NAME : prpcBinder.name());
        binderDefine.setOrder(prpcBinder == null ? 0 : prpcBinder.order());
        return binderDefine;
    }
    
    public <T> Binder<T> getBinderDefine(Class<T> interfaceClass, String name) {
        Map<String, Binder<?>> map = this.binderMap.get(interfaceClass);
        if (map != null) {
            return (Binder<T>) map.get(name);
        }
        return null;
    }

    public <T> Binder<T> getBinderDefine(Class<T> interfaceClass) {
        return getBinderDefine(interfaceClass, Constants.DEFAULT_BINDER_NAME);
    }

    public <T> Map<String, Binder<?>> getBinderDefines(Class<T> interfaceClass) {
        return this.binderMap.get(interfaceClass);
    }
}
