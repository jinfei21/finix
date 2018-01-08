package com.finix.framework.filter;

import com.finix.framework.core.Spi;
import com.finix.framework.rpc.Caller;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;

@Spi
public interface Filter {

    Response filter(Caller caller, Request request);

    boolean defaultEnable();

    int getOrder();
}