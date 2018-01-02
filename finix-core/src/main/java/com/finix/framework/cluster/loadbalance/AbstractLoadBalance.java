package com.finix.framework.cluster.loadbalance;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.finix.framework.cluster.LoadBalance;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractLoadBalance implements LoadBalance{

    protected static final int MAX_REFER_COUNT = 10;
    
    protected AtomicReference<List<Refer>> refersRef = new AtomicReference<>();

	@Override
	public void onRefresh(List<Refer> refers) {
		this.refersRef.set(refers);
		
	}

	@Override
	public Refer select(Request request) {
		List<Refer> refers = this.refersRef.get();
		
		Refer refer = null;
		if(refers.size() > 1){
			refer = doSelect(request);
		}else if(refers.size() == 1){
			refer = refers.get(0).isAvailable() ? refers.get(0):null;
		}
		
        if (refer != null) {
            return refer;
        }
        throw new FinixServiceException(this.getClass().getSimpleName() + " No available refers for call ");    
	}

	
	@Override
	public List<Refer> selectToHolder(Request request) {
	
		List<Refer> refers = this.refersRef.get();
		
		List<Refer> refersHolder = Collections.emptyList();
		
		if(refers == null){
			throw new FinixServiceException(this.getClass().getSimpleName() + " No available refers for call : refers_size= 0 ");
		}
		
		if(refers.size() > 1){
			refersHolder = doSelectToHolder(request);
		}else if(refers.size() == 1 && refers.get(0).isAvailable()){
			refersHolder = Collections.singletonList(refers.get(0));
		}
		
		if(refersHolder.isEmpty()){
            throw new FinixServiceException(this.getClass().getSimpleName()
                    + " No available refers for call : refers_size=" + refers.size());
		}
		
		return refersHolder;
	}

    protected List<Refer> getRefers() {
        return refersRef.get();
    }

    protected abstract Refer doSelect(Request request);

    protected abstract List<Refer> doSelectToHolder(Request request);
    
}
