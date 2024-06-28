package fluff.network.simple;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import fluff.network.NetworkException;
import fluff.network.server.AbstractServer;

/**
 * A simple implementation of the {@link AbstractServer} class for managing server connections.
 */
public class SimpleServer extends AbstractServer {
    
    /**
     * Constructs a new SimpleServer.
     * 
     * @param port the port on which the server will listen for connections
     */
    public SimpleServer(int port) {
        super(port);
    }
    
    @Override
    protected void createConnection(Socket socket) throws IOException, NetworkException {
        SimpleClientConnection client = new SimpleClientConnection(this, UUID.randomUUID(), socket, defaultContext, defaultHandlerFunc.invoke(), defaultChannelFunc.invoke());
        
        if (!client.isConnected()) client.disconnect();
    }
}
