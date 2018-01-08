package com.finix.framework.proto;

import java.util.Random;

public class SimpleImpl implements Simple {

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        String hello = "Hello " + request.getName() + ". " + new Random().nextInt(10000);
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
