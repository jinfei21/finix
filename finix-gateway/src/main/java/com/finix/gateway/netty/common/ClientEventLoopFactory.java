package com.finix.gateway.netty.common;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public interface ClientEventLoopFactory {

    EventLoopGroup newClientWorkerEventLoopGroup();

    Class<? extends SocketChannel> clientSocketChannelClass();
}
