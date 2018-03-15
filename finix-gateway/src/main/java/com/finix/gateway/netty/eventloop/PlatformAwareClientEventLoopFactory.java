package com.finix.gateway.netty.eventloop;

import com.finix.gateway.netty.common.ClientEventLoopFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlatformAwareClientEventLoopFactory implements ClientEventLoopFactory {

    private final ClientEventLoopFactory delegate;
    
    public PlatformAwareClientEventLoopFactory(String name, int clientWorkerThreadsCount) {
        if (Epoll.isAvailable()) {
            log.info("Epoll is available so using the native socket transport.");
            delegate = new EpollClientEventLoopFactory(name, clientWorkerThreadsCount);
        } else {
            log.info("Epoll not available Using nio socket transport.");
            delegate = new NioClientEventLoopFactory(name, clientWorkerThreadsCount);
        }
    }
	
	@Override
	public EventLoopGroup newClientWorkerEventLoopGroup() {
		return delegate.newClientWorkerEventLoopGroup();
	}

	@Override
	public Class<? extends SocketChannel> clientSocketChannelClass() {
		return delegate.clientSocketChannelClass();
	}
	
}
