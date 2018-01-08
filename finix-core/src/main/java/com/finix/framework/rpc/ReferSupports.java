package com.finix.framework.rpc;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReferSupports {

    private static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);

    private static final int DELAY_TIME = 1000;

    public static void delayDestroy(final List<Refer> refers) {
        if (refers == null || refers.isEmpty()) {
            return;
        }

        scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                for (Refer refer : refers) {
                    try {
                        refer.destroy();
                    } catch (Exception e) {
                        log.error("ReferSupports delayDestroy Error: referUrl=" + refer.getServiceUrl().getUri(), e);
                    }
                }
            }
        }, DELAY_TIME, TimeUnit.MILLISECONDS);

        log.info("ReferSupports delayDestroy Success: size={} service={} serviceUrls={}", refers.size(), refers.get(0).getReferUrl()
                .getIdentity(), getServerPorts(refers));
    }

    private static String getServerPorts(List<Refer> refers) {
        if (refers == null || refers.isEmpty()) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Refer refer : refers) {
            builder.append(refer.getServiceUrl().getServerPortStr()).append(",");
        }
        builder.setLength(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }
}