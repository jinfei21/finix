package com.finix.framework.serialize;

import java.io.IOException;

import com.finix.framework.exception.FinixServiceException;
import com.google.protobuf.Message;

public abstract class ProtobufSerialization implements Serialization {

    @Override
    public byte[] serialize(Object obj) {
        if (obj instanceof Message)
            return serializeMessage((Message) obj);
        throw new FinixServiceException(String.format("object [%s] is not protobuf Message, can not serialize.", obj));
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        if (!Message.class.isAssignableFrom(clazz)) {
            throw new FinixServiceException(String.format("class [%s] is not protobuf Message, can not serialize.", clazz.getName()));
        }
        Class<? extends Message> type = (Class<? extends Message>) clazz;
        return (T) deserializeMessage(bytes, type);
    }

    
    public abstract byte[] serializeMessage(Message message);
    
    public abstract <T extends Message> T deserializeMessage(byte[] bytes, Class<T> clazz);
}
