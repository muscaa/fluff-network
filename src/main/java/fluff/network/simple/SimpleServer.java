package fluff.network.simple;

import java.util.UUID;

import fluff.network.server.AbstractClientConnection;
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
    protected AbstractClientConnection createConnection() {
        return new SimpleClientConnection(this, UUID.randomUUID(), defaultContext, defaultHandlerFunc.invoke(), defaultChannelFunc.invoke());
    }
}
