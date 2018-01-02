package com.finix.framework.cluster;

public interface LoadBalanceFactory {

	LoadBalance newInstance(String name);
}
