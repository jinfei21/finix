package com.finix.framework.core;

import com.finix.framework.common.Constants;
import com.finix.framework.exception.FinixFrameworkException;

public class BinderSupporter {

    public static <T> T newInstance(Class<? extends T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new FinixFrameworkException(String.format("Can not create instance by class %s error.", clazz), e);
        }
    }

    public static <T> T generate(Class<T> clazz) {
        Binder<?> binderDefine = BinderFactory.getInstance().getBinderDefine(clazz, Constants.DEFAULT_BINDER_NAME);
        if (binderDefine == null) {
            return null;
        }
        return (T) newInstance(binderDefine.getBinderClass());
    }

    public static <T> T generate(Class<T> clazz, String name) {
        Binder<?> binderDefine = BinderFactory.getInstance().getBinderDefine(clazz, name);
        if (binderDefine == null) {
            return null;
        }
        return (T) newInstance(binderDefine.getBinderClass());
    }
}
