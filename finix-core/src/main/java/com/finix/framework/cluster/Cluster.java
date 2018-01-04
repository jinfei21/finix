package com.finix.framework.cluster;

import java.util.List;

import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.URL;

public interface Cluster {

	String getInterfaceClass();
	void init();
	void destroy();
	void onRefresh(List<URL> serviceUrls);
	List<Refer> getRefers();
	URL getReferUrl();
}

