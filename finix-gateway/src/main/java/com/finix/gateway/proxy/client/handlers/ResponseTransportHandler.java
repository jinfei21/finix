package com.finix.gateway.proxy.client.handlers;

import java.util.concurrent.TimeUnit;

import com.finix.gateway.common.GateHttpRequest;
import com.finix.gateway.netty.common.Origin;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseTransportHandler extends SimpleChannelInboundHandler {
	
    private final Origin origin;
    private final long idleTimeoutMillis;
    private final GateHttpRequest request;
    
    
    public ResponseTransportHandler(GateHttpRequest request,Origin origin){
    	this(request,origin,TimeUnit.SECONDS,5L);
    }
    
    public ResponseTransportHandler(GateHttpRequest request,Origin origin,TimeUnit timeUnit,long idlTimeout){
    	this.request = request;
    	this.origin = origin;
    	this.idleTimeoutMillis = timeUnit.toMillis(idlTimeout);
    }
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {		
		
		
		
	}

	
}
