package com.finix.framework.serialize;

import com.finix.framework.core.SpiBinder;
import com.finix.framework.util.ProtoBuffUtil;
import com.google.protobuf.Message;

@SpiBinder(name = "protobuf.bin")
public class ProtobufBinSerialization extends ProtobufSerialization {

    public static final String NAME = "protobuf.bin";

    @Override
    public byte[] serializeMessage(Message message) {
        return message.toByteArray();
    }

    @Override
    public <T extends Message> T deserializeMessage(byte[] bytes, Class<T> clazz) {
        return ProtoBuffUtil.byteArrayToProtobuf(bytes, clazz);
    }

    @Override
    public String getName() {
        return NAME;
    }
}