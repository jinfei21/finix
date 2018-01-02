package com.finix.framework.cluster;

public interface HaStrategyFactory {

	HaStrategy getInstance(String name);
}
