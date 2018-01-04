package com.finix.framework.serialize;

import java.io.UnsupportedEncodingException;

import com.finix.framework.common.Constants;
import com.finix.framework.core.SpiBinder;
import com.finix.framework.util.ProtoBuffUtil;
import com.google.protobuf.Message;

@SpiBinder(name = "protobuf.json")
public class ProtobufJsonSerialization extends ProtobufSerialization {

    public static final String NAME = "protobuf.json";

    @Override
    public byte[] serializeMessage(Message message) {
        try {
            String jsonString = ProtoBuffUtil.convertProtoBuffToJson(message);
            return jsonString.getBytes(Constants.DEFAULT_CHARACTER);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported Encoding " + Constants.DEFAULT_CHARACTER, e);
        }
    }

    @Override
    public <T extends Message> T deserializeMessage(byte[] bytes, Class<T> clazz) {
        try {
            return ProtoBuffUtil.convertJsonToProtoBuff(new String(bytes, Constants.DEFAULT_CHARACTER), clazz);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported Encoding " + Constants.DEFAULT_CHARACTER, e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}