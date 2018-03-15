package com.finix.gateway.common;

import com.finix.gateway.netty.common.Origin;

public interface Connection {
	
    /**
     * Returns if the underlying connection is still active.
     *
     * @return if the underlying connection is still active
     */
    boolean isConnected();
    
    /**
     * Returns the endpoint for this connection.
     *
     * @return the endpoint for this connection
     */
    Origin getOrigin();
    
    /**
     * Register a listener connection state events.
     *
     * @param listener listener to register
     */
    void addConnectionListener(ConnectionListener listener);

    /**
     * Closes the connection.
     */
    void close();
    
    void send();

}
