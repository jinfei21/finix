package com.finix.framework.core;

import lombok.Data;

@Data
public class Binder<T> {

    private Class<T> interfaceClass;
    private String name;
    private Class<? extends T> binderClass;
    private Scope scope;
    private int order;
}
