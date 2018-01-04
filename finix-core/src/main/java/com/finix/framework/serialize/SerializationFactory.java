package com.finix.framework.serialize;



public interface SerializationFactory {

    Serialization newInstance(String name);

}