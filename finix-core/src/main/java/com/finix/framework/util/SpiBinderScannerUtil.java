package com.finix.framework.util;

import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.finix.framework.core.Spi;
import com.finix.framework.core.SpiBinder;
import com.finix.framework.exception.FinixFrameworkException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ClassAnnotationMatchProcessor;

public class SpiBinderScannerUtil {


    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/finix.factories";
    
	public static Map<Class<?>,List<Class<?>>> scanBinders(List<String> pkages){
		
		List<Class<?>> spiList = Lists.newArrayList();
		List<Class<?>> binderList = Lists.newArrayList();
		
		new FastClasspathScanner(pkages.toArray(new String[0])).matchClassesWithAnnotation(Spi.class, new ClassAnnotationMatchProcessor() {
			
			@Override
			public void processMatch(Class<?> clazz) {
				spiList.add(clazz);
				
			}
		}).matchClassesWithAnnotation(SpiBinder.class, new ClassAnnotationMatchProcessor() {
			
			@Override
			public void processMatch(Class<?> clazz) {
				binderList.add(clazz);
			}
		}).scan();
		
		Map<Class<?> ,List<Class<?>>> binderMap = Maps.newHashMap();
		
		for(Class<?> binderClazz:binderList){
			if(!binderClazz.isInterface()){
				for(Class<?> spiClazz:spiList){
					if(spiClazz.isInterface()){
						if(spiClazz.isAssignableFrom(binderClazz)){
							List<Class<?>> values = binderMap.get(spiClazz);
							if(values == null){
								values = Lists.newArrayList();
								binderMap.put(spiClazz, values);
							}
							values.add(binderClazz);
						}
					}
				}
			}
		}
		
		return binderMap;
	}
	
	public static Map<Class<?>,List<Class<?>>> scanBinders(){
		return scanBinders(Lists.newArrayList());
	}
	
	public static Map<Class<?>,List<Class<?>>> loadBinder(ClassLoader loader){
		
		if(loader == null){
			loader = SpiBinderScannerUtil.class.getClassLoader();
		}
		
        ClassLoader classLoader = loader;
        try {
            Enumeration<URL> urls = (classLoader != null ? classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
                    ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
            
            Map<Class<?>,List<Class<?>>> binderMap = Maps.newLinkedHashMap();
            
            while(urls.hasMoreElements()){
            	URL url = urls.nextElement();
            	Properties props = new Properties();
            	props.load(url.openStream());
            	for(String key:props.stringPropertyNames()){
                    Class<?> spiInterface = loadClass(key, classLoader);
                    String[] strs = props.getProperty(key).split(",");
                    List<String> binders = Arrays.asList(strs);
                    List<Class<?>> binderClasses = Lists.newArrayList();
                    for (Object binder : binders) {
                        Class<Object> binderClass = loadClass(binder.toString(), classLoader);
                        binderClasses.add(binderClass);
                    }
                    binderMap.put(spiInterface, binderClasses);
            	}
            	
            }
            return binderMap;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to load  binders from location [" + FACTORIES_RESOURCE_LOCATION + "]", ex);
        }
	}
	
    private static <T> Class<T> loadClass(String className, ClassLoader classLoader) {
        if (StringUtils.isBlank(className)) {
            return null;
        }
        Class<T> clz;
        try {
            clz = (Class<T>) Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new FinixFrameworkException(String.format("Class %s not found.", className));
        }
        return clz;
    }
}
