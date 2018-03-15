package com.finix.gateway.netty.common;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public interface ServerEventLoopFactory {

    EventLoopGroup newBossEventLoopGroup();

    EventLoopGroup newWorkerEventLoopGroup();

    Class<? extends ServerChannel> serverChannelClass();

}