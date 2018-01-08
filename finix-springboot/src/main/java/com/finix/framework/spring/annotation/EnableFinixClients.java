package com.finix.framework.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import com.finix.framework.spring.autoconfig.FinixClientReferProcessor;
import com.finix.framework.spring.autoconfig.FinixClientsRegistrar;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({FinixClientsRegistrar.class, FinixClientReferProcessor.class})
public @interface EnableFinixClients {

    @AliasFor("basePackageClasses")
    Class<?>[] value() default {};

    @AliasFor("value")
    Class<?>[] basePackageClasses() default {};

    String[] basePackages() default {};
}