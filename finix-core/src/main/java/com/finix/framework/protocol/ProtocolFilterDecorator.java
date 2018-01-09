package com.finix.framework.protocol;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;

import com.finix.framework.common.URLParamType;
import com.finix.framework.core.Binder;
import com.finix.framework.core.BinderFactory;
import com.finix.framework.core.BinderSupporter;
import com.finix.framework.exception.ErrorMsgConstants;
import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.filter.Filter;
import com.finix.framework.rpc.Exporter;
import com.finix.framework.rpc.Protocol;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.URL;
import com.google.common.collect.Lists;

public class ProtocolFilterDecorator  implements Protocol {

    private Protocol protocol;
    private List<Filter> filters;
    
    public ProtocolFilterDecorator(Protocol protocol, List<Filter> filters) {
        if (protocol == null) {
            throw new FinixFrameworkException("Protocol is null when construct ProtocolFilterDecorator",
                    ErrorMsgConstants.FRAMEWORK_INIT_ERROR);
        }
        this.protocol = protocol;
        this.filters = filters;
        initFilters();
    }
    

	@Override
	public Exporter export(Provider provider, URL serviceUrl) {
		return protocol.export(decorateWithFilter(provider, serviceUrl), serviceUrl);
	}
	
	@Override
	public Refer refer(String interfaceClass, URL referUrl, URL serviceUrl) {
		return decorateWithFilter(protocol.refer(interfaceClass, referUrl, serviceUrl), referUrl);
	}
	
    private Refer decorateWithFilter(Refer refer, URL referUrl) {
        List<Filter> filters = getFilters(referUrl);
        Refer lastRefer = refer;
        for (Filter filter : filters) {
            final Filter filterFinal = filter;
            final Refer lastReferFinal = lastRefer;
            
            lastRefer = new Refer() {

				@Override
				public Response call(Request request) {
					return filterFinal.filter(lastReferFinal, request);
				}

				@Override
				public String getInterface() {
					return refer.getInterface();
				}

				@Override
				public boolean isAvailable() {
					return refer.isAvailable();
				}

				@Override
				public URL getReferUrl() {
					return refer.getReferUrl();
				}

				@Override
				public URL getServiceUrl() {
					return refer.getServiceUrl();
				}

				@Override
				public void init() {
					refer.init();
				}

				@Override
				public void destroy() {
					refer.destroy();
				}
            	
            };
        }
        
        return lastRefer;
    }
	
    private  Provider decorateWithFilter(Provider provider, URL serviceUrl) {
        List<Filter> filters = getFilters(serviceUrl);
        if (filters == null || filters.size() == 0) {
            return provider;
        }
        
        Provider lastProvider = provider;
        
        for(Filter filter:filters){
        	final Filter filterFinal = filter;
            final Provider lastProviderFinal = lastProvider;

            lastProvider = new Provider() {

				@Override
				public Response call(Request request) {
                    return filterFinal.filter(lastProviderFinal, request);
				}

				@Override
				public Method lookupMethod(String methodName, String paramDesc) {
                    return provider.lookupMethod(methodName, paramDesc);
				}

				@Override
				public String getInterface() {
					return provider.getInterface();
				}

                @Override
                public URL getServiceUrl() {
                    return provider.getServiceUrl();
                }

                @Override
                public void setServiceUrl(URL serviceUrl) {
                    provider.setServiceUrl(serviceUrl);
                }

				@Override
				public void destroy() {
					provider.destroy();
				}

				@Override
				public void init() {
					 provider.init();					
				}
            	
			};
        }
        return lastProvider;

    }
	
    private void initFilters() {
        if (this.filters == null) {
            this.filters = Lists.newArrayList();
        }

        Map<String, Binder<?>> binderMap = BinderFactory.getInstance().getBinderDefines(Filter.class);
        for (Binder<?> binderDefine : binderMap.values()) {
            Filter filter = (Filter) BinderSupporter.newInstance(binderDefine.getBinderClass());
            this.filters.add(filter);
        }
    }

    /**
     * @param url referUrl or serviceUrl
     * @return
     */
    private List<Filter> getFilters(URL url) {
        List<Filter> enableFilers = Lists.newArrayList();
        for (Filter filter : this.filters) {
            Boolean urlFilterEnable = url.getBooleanParameter(URLParamType.filter.name());
            if ((urlFilterEnable == null && filter.defaultEnable())
                    || BooleanUtils.isTrue(urlFilterEnable)) {
                enableFilers.add(filter);
            }
        }

        enableFilers.sort(new Comparator<Filter>() {
            @Override
            public int compare(Filter o1, Filter o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        Collections.reverse(enableFilers);
        return enableFilers;
    }
    
	@Override
	public void destroy() {
		protocol.destroy();
	}
	
	@Override
	public String getName() {
		return protocol.getName();
	}
}
