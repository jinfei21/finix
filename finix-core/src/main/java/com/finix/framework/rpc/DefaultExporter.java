package com.finix.framework.rpc;

import lombok.Getter;

@Getter
public class DefaultExporter implements Exporter {

	private URL serviceUrl;

	private Provider provider;

	public DefaultExporter(Provider provider, URL serviceUrl) {
		this.provider = provider;
		this.serviceUrl = serviceUrl;
	}
}
