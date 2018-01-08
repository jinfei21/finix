package com.finix.framework.serialize;

import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;

import com.finix.framework.common.URLParamType;
import com.finix.framework.core.BinderSupporter;
import com.finix.framework.exception.FinixServiceException;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

public class ProtobufSerializationFactory implements SerializationFactory {

    @Setter
    @Getter
    private String defaultName = URLParamType.serialization.getValue();

    private final ConcurrentMap<String, Serialization> cached = Maps.newConcurrentMap();

    @Override
    public Serialization getInstance(String name) {
        if (StringUtils.isBlank(name)) {
            name = defaultName;
        }
        Serialization serialization = cached.get(name);
        if (serialization == null) {
        	serialization = BinderSupporter.generate(Serialization.class, name);
        	if(serialization == null){
        		throw new FinixServiceException("Can not create Serialization instance by name: " + name);
        	}
        	cached.putIfAbsent(name, serialization);
        	
        }
        return serialization;

    }
}