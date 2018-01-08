package com.finix.framework.demo.server.service;

import org.apache.commons.lang3.RandomUtils;

import com.finix.framework.proto.Helloworld;
import com.finix.framework.proto.Simple;
import com.finix.framework.spring.annotation.FinixServiceComponent;

@FinixServiceComponent
public class SimpleImpl implements Simple {

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt();
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
