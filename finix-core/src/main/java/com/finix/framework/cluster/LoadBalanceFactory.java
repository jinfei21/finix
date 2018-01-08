package com.finix.framework.cluster;

public interface LoadBalanceFactory {

	LoadBalance getInstance(String name);
}
