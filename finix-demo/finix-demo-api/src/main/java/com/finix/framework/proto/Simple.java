package com.finix.framework.proto;

import com.finix.framework.annotation.FinixInterface;

@FinixInterface()
public interface Simple {

    Helloworld.HelloReply sayHello(Helloworld.HelloRequest request);

}
