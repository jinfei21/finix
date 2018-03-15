package com.finix.gateway.proxy.server;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.finix.gateway.config.NettyServerConfig;

@JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
public class ProxyServerBuilder {

    public NettyServerConfig build() {
        return new NettyServerConfig(this);
    }
    
	public static void main(String args[]){
		ProxyServerBuilder b = new ProxyServerBuilder();
		NettyServerConfig config = b.build();
		
		System.out.println(config.toString());
	}
}
