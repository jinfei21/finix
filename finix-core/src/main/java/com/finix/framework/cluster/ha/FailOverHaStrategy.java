package com.finix.framework.cluster.ha;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.finix.framework.cluster.LoadBalance;
import com.finix.framework.common.URLParamType;
import com.finix.framework.core.SpiBinder;
import com.finix.framework.exception.FinixFrameworkException;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.URL;
import com.finix.framework.util.ExceptionUtil;

import lombok.extern.slf4j.Slf4j;

@SpiBinder(name = "failover")
@Slf4j
public class FailOverHaStrategy extends AbstractHaStrategy{

	@Override
	public String getStrategyName() {
		return "failover";
	}

	@Override
	public Response call(Request request, LoadBalance loadBalance) {
		
		List<Refer> refers = loadBalance.selectToHolder(request);
		
		if(CollectionUtils.isEmpty(refers)){
			throw new FinixServiceException(String.format("FailoverHaStrategy No refers for request:%s, loadbalance:%s", request, loadBalance));
		}
		
		URL referURL = refers.get(0).getReferUrl();
		
		int tryCount = referURL.getMethodParameter(request.getMethodName(), URLParamType.retries.getName(), URLParamType.retries.getIntValue());
				
		if(tryCount < 0){
			tryCount = 0;
		}
		
		for(int i=0;i<= tryCount;i++){
			Refer refer = refers.get(i%refers.size());
			try{
				request.setRetries(i);
				return refer.call(request);
			}catch(RuntimeException e){
                // 对于业务异常，直接抛出
                if (ExceptionUtil.isBizException(e)) {
                    throw e;
                } else if (i >= tryCount) {
                    throw e;
                }
                log.warn(String.format("FailoverHaStrategy Call false for request:%s error=%s", request, e.getMessage()));
			}
		}
        throw new FinixFrameworkException("FailoverHaStrategy.call should not come here!");
	}

	
}
