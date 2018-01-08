package com.finix.framework.filter;

import org.apache.commons.lang.StringUtils;

import com.finix.framework.common.Constants;
import com.finix.framework.common.Info;
import com.finix.framework.common.URLParamType;
import com.finix.framework.exception.ErrorMsgConstants;
import com.finix.framework.exception.FinixServiceException;
import com.finix.framework.rpc.Caller;
import com.finix.framework.rpc.Provider;
import com.finix.framework.rpc.Refer;
import com.finix.framework.rpc.Request;
import com.finix.framework.rpc.Response;
import com.finix.framework.rpc.URL;
import com.finix.framework.util.NetUtil;

public abstract class AbstractFilter implements Filter {

    protected URL getCallerUrl(Caller caller) {
        if (caller instanceof Provider) {
            return ((Provider) caller).getServiceUrl();
        } else if (caller instanceof Refer) {
            return ((Refer) caller).getReferUrl();
        }
        throw new FinixServiceException("Caller must be Provider or Refer, but is " + caller.getClass());
    }

    protected String getNodeType(Caller caller) {
        return caller instanceof Provider ? Constants.NODE_TYPE_SERVICE : Constants.NODE_TYPE_REFER;
    }

    protected String getClientHost(Caller caller, Request request) {
        return (caller instanceof Provider)
                ? request.getAttachments().get(URLParamType.clientHost.getName())
                : NetUtil.getLocalIp();
    }

    protected String getServerHost(Caller caller) {
        return (caller instanceof Provider)
                ? ((Provider) caller).getServiceUrl().getServerPortStr()
                : ((Refer) caller).getServiceUrl().getServerPortStr();
    }

    protected String getParameterTypes(Request request) {
        return StringUtils.join(request.getParameterTypes());
    }

    protected String getInterfaceVersion(Caller caller) {
        URL url = getCallerUrl(caller);
        return url.getVersion();
    }

    protected String getAppId() {
        return Info.getInstance().getAppId();
    }

    protected String getFinixVersion(Caller caller) {
        return Info.getInstance().getVersion();
    }

    protected String getProtocol(Caller caller) {
        URL url = getCallerUrl(caller);
        return url.getProtocol();
    }

    protected String getRequestPayloadSize(Request request) {
        return request.getAttachments().get(URLParamType.requestPayloadSize.getName());
    }

    protected String getResponsePayloadSize(Response response) {
        return response == null ? "" : response.getAttachments().get(URLParamType.responsePayloadSize.getName());
    }

    protected String getStatusCode(Caller caller, Response response) {
        if (caller instanceof Provider) {
            if (response == null) {
                return String.valueOf(ErrorMsgConstants.SERVICE_DEFAULT_ERROR_CODE);
            } else if (response.getException() != null) {
                return String.valueOf(ErrorMsgConstants.BIZ_DEFAULT_ERROR_CODE);
            } else {
                return String.valueOf(ErrorMsgConstants.SUCCESS);
            }
        }
        return String.valueOf(response == null ? "" : response.getCode());
    }

    protected String getProcessTime(Caller caller, Response response, long requestTime) {
        return (caller instanceof Provider) ? String.valueOf(requestTime)
                : String.valueOf(response.getProcessTime());
    }

    protected String getRetries(Request request) {
        return String.valueOf(request.getRetries());
    }
}