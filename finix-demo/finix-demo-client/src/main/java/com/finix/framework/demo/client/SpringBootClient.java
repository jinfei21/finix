package com.finix.framework.demo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import com.finix.framework.proto.Simple;
import com.finix.framework.spring.annotation.EnableFinixClients;

@SpringBootApplication
@Configuration
@EnableFinixClients(basePackageClasses = {Simple.class})
public class SpringBootClient {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootClient.class, args);
    }

}
