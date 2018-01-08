package com.finix.framework.protocol;

import com.finix.framework.rpc.DefaultExporter;
import com.finix.framework.rpc.Exporter;
import com.finix.framework.rpc.Protocol;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.URL;

public abstract class AbstractProtocol implements Protocol{



	@Override
	public  Exporter export(Provider provider, URL serviceUrl) {

		serviceUrl.setProtocol(this.getName());
		URL newServiceUrl = this.deploy(provider, serviceUrl);
		Exporter exporter = new DefaultExporter(provider, newServiceUrl);
		provider.setServiceUrl(newServiceUrl);
		return exporter;
	}

	public abstract  URL deploy(Provider provider,URL serviceUrl);


	@Override
	public void destroy() {

		
	}

	
}
