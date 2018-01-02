package com.finix.framework.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.finix.framework.common.Constants;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SpiBinder {
	
    String name() default Constants.DEFAULT_BINDER_NAME;

    int order() default 0;
}
