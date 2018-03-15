package com.finix.gateway.config;

import org.junit.Test;

import com.finix.gateway.yaml.YamlConfig;

public class NettyConfigTest {

	
	@Test
	public void testHttpConfig(){
        String yaml = "" +
                "proxy:\n" +
                "  connectors:\n" +
                "      http:\n" +
                "        port: 8080\n" +
                "      https:\n" +
                "        port: 8443\n" +
                "        certFile: example.keystore\n" +
                "        certKeyFile: example\n" +
                "        validateCerts: true";

        YamlConfig yamlConfig = new YamlConfig(yaml);
        
		
	}
}
