package com.finix.gateway.common;

public interface RequestOperation<T extends Connection> {

	void execute(T connection);

}
