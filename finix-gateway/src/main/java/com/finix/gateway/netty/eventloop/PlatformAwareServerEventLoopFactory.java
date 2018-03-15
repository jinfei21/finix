package com.finix.gateway.netty.eventloop;

import com.finix.gateway.netty.common.ServerEventLoopFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlatformAwareServerEventLoopFactory implements ServerEventLoopFactory {

    private final ServerEventLoopFactory delegate;
    

    public PlatformAwareServerEventLoopFactory(String name, int bossThreadsCount, int workerThreadsCount) {
        if (Epoll.isAvailable()) {
            log.info("Epoll is available so using the native socket transport.");
            delegate = new EpollServerEventLoopFactory(name, bossThreadsCount, workerThreadsCount);
        } else {
            log.info("Epoll not available Using nio socket transport.");
            delegate = new NioServerEventLoopFactory(name, bossThreadsCount, workerThreadsCount);
        }
    	
    }
    
	@Override
	public EventLoopGroup newBossEventLoopGroup() {
		return delegate.newBossEventLoopGroup();
	}

	@Override
	public EventLoopGroup newWorkerEventLoopGroup() {
		return delegate.newWorkerEventLoopGroup();
	}

	@Override
	public Class<? extends ServerChannel> serverChannelClass() {
		return delegate.serverChannelClass();
	}
}
