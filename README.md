# Finix
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/weibocom/motan/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.weibo/motan.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.weibo%22%20AND%20motan)
[![Build Status](https://img.shields.io/travis/weibocom/motan/master.svg?label=Build)](https://travis-ci.org/weibocom/motan)
[![OpenTracing-1.0 Badge](https://img.shields.io/badge/OpenTracing--1.0-enabled-blue.svg)](http://opentracing.io)
[![Skywalking Tracing](https://img.shields.io/badge/Skywalking%20Tracing-enable-brightgreen.svg)](https://github.com/OpenSkywalking/skywalking)

# Overview
Finix is a cross-language remote procedure call(RPC) framework for rapid development of high performance distributed services. 



# Features
- Create distributed services without writing extra code.
- Provides cluster support and integrate with popular service discovery services like [Etcd][etcd] or [Zookeeper][zookeeper]. 
- Supports advanced scheduling features like weighted load-balance, scheduling cross IDCs, etc.
- Optimization for high load scenarios, provides high availability in production environment.
- Supports both synchronous and asynchronous calls.
- Support cross-language interactive with C#, JAVA, Python, etc.

# Quick Start

The quick start gives very basic example of running client and server on the same machine. For the detailed information about using and developing Motan, please jump to [Documents](#documents).

> The minimum requirements to run the quick start are: 
>  * JDK 1.7 or above
>  * A java-based project management software like [Maven][maven] or [Gradle][gradle]

## Synchronous calls

1. Add dependencies to pom.

```xml
    <dependency>
        <groupId>com.finix.framework</groupId>
        <artifactId>finix-core</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- dependencies blow were only needed for spring-based features -->
    <dependency>
        <groupId>com.finix.framework</groupId>
        <artifactId>finix-springboot</artifactId>
        <version>1.0.0</version>
    </dependency>

```

2. Create an interface for both service provider and consumer.



# Documents

* [Wiki  TBD](TBD)


# Contributors

* 费永军([@feiyongjun](https://github.com/jinfei21))
* 黄印煌([@huangyinghua](https://github.com/jinfei21))

# License

Finix is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

[maven]:https://maven.apache.org
[gradle]:http://gradle.org
[consul]:http://www.consul.io
[zookeeper]:http://zookeeper.apache.org


