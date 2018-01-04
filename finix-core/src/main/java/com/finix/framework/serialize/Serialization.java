package com.finix.framework.serialize;

import java.io.IOException;

import com.finix.framework.core.Scope;
import com.finix.framework.core.Spi;

@Spi(scope = Scope.PROTOTYPE)
public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;

    String getName();
}