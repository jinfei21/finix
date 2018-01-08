package com.finix.framework.spring.autoconfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import com.finix.framework.annotation.FinixInterface;
import com.finix.framework.annotation.FinixService;
import com.finix.framework.common.URLParamType;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.protocol.FinixProtocol;
import com.finix.framework.registry.Registry;
import com.finix.framework.rpc.DefaultProvider;
import com.finix.framework.rpc.Exporter;
import com.finix.framework.rpc.Protocol;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.URL;
import com.finix.framework.spring.util.AopUtil;
import com.google.common.collect.Maps;

@Configuration
public class FinixServiceProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //bean字段上有@BeamService注解，设置@BeamService注解的bean
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                FinixService finixService = field.getAnnotation(FinixService.class);
                if (finixService != null) {
                    Object value = getFinixServiceBean(field.getType());
                    if (value != null) {
                        field.set(bean, value);
                    }
                }
            } catch (Exception e) {
                throw new BeanInitializationException("Failed to init remote service reference at filed " + field.getName()
                        + " in class " + bean.getClass().getName(), e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object springBean, String beanName) throws BeansException {
        Object bean = springBean;
        if (AopUtils.isAopProxy(bean)) {
            try {
                bean = AopUtil.getTarget(bean);
            } catch (Exception e) {
                throw new FinixServiceException(String.format("Can not find proxy target for %s .", bean.getClass().getName()));
            }
        } else {
            bean = springBean;
        }
        Annotation annotation = AnnotationUtils.findAnnotation(bean.getClass(), FinixService.class);
        if (annotation == null) {
            return springBean;
        }
        List<Class<?>> interfaceClasses = findAllInterfaces(bean);
        if (interfaceClasses.size() == 0) {
            throw new FinixServiceException(String.format("Can not find %s serviceImpl's interface.", bean.getClass().getName()));
        }
        for (Class<?> interfaceClass : interfaceClasses) {
            registryService(interfaceClass, springBean);
        }
        return springBean;
    }


    private List<Class<?>> findAllInterfaces(Object bean) {
        List<Class<?>> beamInterfaces = new ArrayList<>();
        Set<Class<?>> interfaceClasses = ClassUtils.getAllInterfacesAsSet(bean);
        for (Class<?> interfaceClass : interfaceClasses) {
            Annotation annotation = AnnotationUtils.findAnnotation(interfaceClass, FinixInterface.class);
            if (annotation != null) {
                beamInterfaces.add(interfaceClass);
            }
        }
        return beamInterfaces;
    }

    private void registryService(Class interfaceClass, Object bean) {
    	FinixService anno = AnnotationUtils.findAnnotation(bean.getClass(), FinixService.class);
    	
        //TODO 找到interfaceClass的参数
    	Map<String, String> parameters = Maps.newHashMap();
    	parameters.put(URLParamType.version.getName(), StringUtils.isBlank(anno.version())?URLParamType.version.getName():anno.version());
        URL serviceUrl = URL.builder().parameters(parameters).build();
        
        //TODO 只支持一种协议
        Protocol protocol = applicationContext.getBean(Protocol.class);
        if (protocol == null) {
            throw new FinixServiceException(String.format("Can not find bean of class %s ", FinixProtocol.class.getName()));
        }
        Provider provider = new DefaultProvider<>(interfaceClass, bean, serviceUrl);
        Exporter exporter = protocol.export(provider, serviceUrl);
        Registry registry = applicationContext.getBean(Registry.class);
        if (registry == null) {
            throw new FinixServiceException(String.format("Can not find bean of class %s ", Registry.class.getName()));
        }
        registry.register(exporter.getServiceUrl());
    }

    private Object getFinixServiceBean(Class<?> serviceClass) {
        Map<String, ?> beans = applicationContext.getBeansOfType(serviceClass);
        for (Object bean : beans.values()) {
            FinixService anno = AnnotationUtils.findAnnotation(bean.getClass(), FinixService.class);
            if (anno != null) {
                return bean;
            }
        }
        return null;
    }
}