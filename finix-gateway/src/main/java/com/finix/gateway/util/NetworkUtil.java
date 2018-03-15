package com.finix.gateway.util;

import static com.google.common.base.Throwables.propagate;

import java.io.IOException;
import java.net.ServerSocket;

public class NetworkUtil {

    /**
     * Returns an available free socket port in the system.
     *
     * @return an available free socket port in the system
     */
    public static int freePort() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            int localPort = serverSocket.getLocalPort();
            serverSocket.close();
            return localPort;
        } catch (IOException e) {
            throw propagate(e);
        }
    }
    
    
    
}
