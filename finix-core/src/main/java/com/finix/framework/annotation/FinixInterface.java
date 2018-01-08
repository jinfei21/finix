package com.finix.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FinixInterface {
	String version() default "1.0";
	
	int requestConnectTimeout() default 20;
	
	int connectTimeout() default 2000;
	
	int socketTimeout() default 35000;
	
}

