package com.finix.gateway.common;

import java.util.EventListener;

public interface ConnectionListener extends EventListener {

    /**
     * Called when a connection has been closed.
     *
     * @param connection the connection that was closed
     */
    void connectionClosed(Connection connection);
}
