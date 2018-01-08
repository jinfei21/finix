package com.finix.framework.demo.client.restfulApi;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finix.framework.annotation.FinixClient;
import com.finix.framework.proto.Helloworld;
import com.finix.framework.proto.Simple;

@Component
@RestController
public class SayHelloController {

    @FinixClient
    private Simple simple;

    @RequestMapping("/sayhello")
    public Object sayHello(@RequestParam("name") String name) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName(name).build();
        Helloworld.HelloReply helloReply = simple.sayHello(helloRequest);
        return helloReply.toString();
    }
}
