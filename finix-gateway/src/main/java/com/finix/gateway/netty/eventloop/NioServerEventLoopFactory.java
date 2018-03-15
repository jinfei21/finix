package com.finix.gateway.netty.eventloop;

import com.finix.gateway.netty.common.ServerEventLoopFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NioServerEventLoopFactory  implements ServerEventLoopFactory {

	private final String name;
	private final int bossThreads;
	private final int workerThreads;
	
	public NioServerEventLoopFactory(String name ,int bossThreads,int workerThreads){
		this.name = name;
		this.bossThreads = bossThreads;
		this.workerThreads = workerThreads;
	}
	
	@Override
	public EventLoopGroup newBossEventLoopGroup() {
		return newEventLoopGroup(bossThreads, name + "-boss-%d-thread");
	}

	@Override
	public EventLoopGroup newWorkerEventLoopGroup() {
		return newEventLoopGroup(workerThreads, name + "-boss-%d-thread");
	}
	
	

	@Override
	public Class<? extends ServerChannel> serverChannelClass() {
        return NioServerSocketChannel.class;
	}
	
    private EventLoopGroup newEventLoopGroup(int threadsCount, String threadsNameFormat) {
        return new NioEventLoopGroup(threadsCount, new ThreadFactoryBuilder()
                .setNameFormat(threadsNameFormat)
                .build());
    }

}
