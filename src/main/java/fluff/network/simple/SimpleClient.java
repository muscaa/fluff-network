package fluff.network.simple;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import fluff.network.NetworkException;
import fluff.network.client.AbstractClient;

/**
 * A simple implementation of the {@link AbstractClient} class for connecting to a server.
 */
public class SimpleClient extends AbstractClient {
    
    /**
     * Connects to the server at the specified host and port.
     * 
     * @param host the hostname of the server
     * @param port the port of the server
     * @throws UnknownHostException if the IP address of the host could not be determined
     * @throws IOException if an I/O error occurs when creating the connection
     * @throws NetworkException if a network-related error occurs when creating the connection
     */
    @SuppressWarnings("resource")
    public void connect(String host, int port) throws UnknownHostException, IOException, NetworkException {
        openConnection(new Socket(host, port));
    }
}
