package com.finix.gateway.netty.eventloop;

import com.finix.gateway.netty.common.ServerEventLoopFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;

public class EpollServerEventLoopFactory implements ServerEventLoopFactory {

	private final String name;
	private final int bossThreads;
	private final int workerThreads;
	
	public EpollServerEventLoopFactory(String name ,int bossThreads,int workerThreads){
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
		return newEventLoopGroup(workerThreads,name + "-worker-%d-thread");
	}

	@Override
	public Class<? extends ServerChannel> serverChannelClass() {
        return EpollServerSocketChannel.class;
	}
	
    private EventLoopGroup newEventLoopGroup(int nThreads, String threadsNameFormat) {
        return new EpollEventLoopGroup(nThreads, new ThreadFactoryBuilder()
                .setNameFormat(threadsNameFormat)
                .build());
    }

}
