package com.finix.gateway.netty.eventloop;

import com.finix.gateway.netty.common.ClientEventLoopFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NioClientEventLoopFactory implements ClientEventLoopFactory {

	private final String name;
	
	private final int workerThreads;
	
	public NioClientEventLoopFactory(String name,int workerThreads){
		this.name = name;
		this.workerThreads = workerThreads;
	}
	
	@Override
	public EventLoopGroup newClientWorkerEventLoopGroup() {
		return new NioEventLoopGroup(workerThreads, new ThreadFactoryBuilder().setNameFormat(name + "-client-worker-%d").build());
	}

	@Override
	public Class<? extends SocketChannel> clientSocketChannelClass() {
		return NioSocketChannel.class;
	}
	

}
