package com.finix.framework.spring.autoconfig;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.finix.framework.annotation.FinixInterface;
import com.finix.framework.spring.annotation.EnableFinixClients;

public class FinixClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware {
    private ResourceLoader resourceLoader;
    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> basePackages = getBasePackages(importingClassMetadata);
        //找到接口
        Set<BeanDefinition> candidateComponents = findInterfaceBeanDefinition(basePackages);
        for (BeanDefinition candidateComponent : candidateComponents) {
            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
            AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
            Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(FinixInterface.class.getCanonicalName());
            registerClient(registry, annotationMetadata, attributes);
        }
    }

    protected void registerClient(BeanDefinitionRegistry registry,
                                  AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String interfaceName = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(FinixClientFactoryBean.class);
        definition.addPropertyValue("interfaceClass", interfaceName);
        definition.addPropertyValue("interfaceVersion", attributes.get("version"));
        definition.addPropertyValue("socketTimeout", attributes.get("socketTimeout"));
        definition.addPropertyValue("requestConnectTimeout", attributes.get("requestConnectTimeout"));
        definition.addPropertyValue("connectTimeout", attributes.get("connectTimeout"));
        
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, interfaceName, new String[]{});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    protected Set<String> getExcludeInterfaces(Set<String> basePackages) {
        Set<String> excludeInterfaces = new HashSet<>();
        Set<BeanDefinition> excludeComponents = findServiceBeanDefinition(basePackages);
        for (BeanDefinition excludeComponent : excludeComponents) {
            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) excludeComponent;
            AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
            String[] interfaceNames = annotationMetadata.getInterfaceNames();
            excludeInterfaces.addAll(Arrays.asList(interfaceNames));
        }
        return excludeInterfaces;
    }

    protected Set<BeanDefinition> findInterfaceBeanDefinition(Set<String> basePackages) {
        return findBeanDefinitionWithAnnotation(basePackages, FinixInterface.class);
    }

    protected Set<BeanDefinition> findServiceBeanDefinition(Set<String> basePackages) {
        return findBeanDefinitionWithAnnotation(basePackages, FinixInterface.class);
    }

    protected Set<BeanDefinition> findBeanDefinitionWithAnnotation(Set<String> basePackages, Class<? extends Annotation> annotation) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            beanDefinitions.addAll(candidateComponents);
        }
        return beanDefinitions;
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableFinixClients.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();
        for (Class<?> clazz : (Class[]) attributes.get("value")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        basePackages.add(
                ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        return basePackages;
    }


    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {

            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (beanDefinition.getMetadata().isInterface()
                            && beanDefinition.getMetadata()
                            .getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(beanDefinition
                            .getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(
                                    beanDefinition.getMetadata().getClassName(),
                                    FinixClientsRegistrar.this.classLoader);
                            return !target.isAnnotation();
                        } catch (Exception ex) {
                            this.logger.error("Could not load target class: "
                                    + beanDefinition.getMetadata().getClassName(), ex);
                        }
                    }
                    return true;
                }
                return false;

            }
        };
    }
}