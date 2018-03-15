package com.finix.gateway.config;

import static java.lang.Runtime.getRuntime;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.finix.gateway.proxy.server.ProxyServerBuilder;

import lombok.Data;

@Data
@JsonDeserialize(builder = ProxyServerBuilder.class)
public class NettyServerConfig {

	private int bossThreadCount = 1;
	
	private int workerThreadCount = getRuntime().availableProcessors() / 2;
	
	private int backlog = 1024;
	
	private boolean tcpNoDelay = true;
	
	private boolean reuseAddress = true;
	
	private boolean keepAlive = true;
	
	private int maxInitialLen = 4096;
	private int maxHeaderSize = 8192;
	private int maxChunkSize = 8192;
	private int maxContentLen = 65536;
	private int requestTimeoutMs = 12000;
	private int keepAliveTimeoutMs = 12000;
	private int maxConnectionCount = 512;
	
	private Optional<HttpConfig> httpConfig;
	private Optional<HttpsConfig> httpsConfig;
	
	
	public NettyServerConfig(){
		this.httpConfig = Optional.of(new HttpConfig(8080));
		this.httpsConfig = Optional.empty();
	}
	
	public NettyServerConfig(@JsonProperty("bossThreadCount")  int bossThreadCount,
			@JsonProperty("workerThreadCount") int workerThreadCount,
			@JsonProperty("backlog") int backlog,
			@JsonProperty("tcpNoDelay") boolean tcpNoDelay,
			@JsonProperty("reuseAddress") boolean reuseAddress,
			@JsonProperty("keepAlive") boolean keepAlive,
			@JsonProperty("maxInitialLen") int maxInitialLen,
			@JsonProperty("maxHeaderSize") int maxHeaderSize,
			@JsonProperty("maxChunkSize") int maxChunkSize,
			@JsonProperty("maxContentLen") int maxContentLen,
			@JsonProperty("requestTimeoutMs") int requestTimeoutMs,
			@JsonProperty("keepAlivetimeoutMs") int keepAlivetimeoutMs,
			@JsonProperty("maxConnectionCount") int maxConnectionCount
			){
		
	}
}

