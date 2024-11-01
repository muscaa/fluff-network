package fluff.network.server;

import fluff.network.NetworkException;
import fluff.network.client.AbstractClient;

/**
 * An abstract implementation of the {@link IClientConnection} interface that provides basic functionality for a client connection.
 */
public abstract class AbstractClientConnection extends AbstractClient implements IClientConnection {
    
    protected final AbstractServer server;
    
    /**
     * Constructs a new client connection with the specified server.
     * 
     * @param server the server to which this client connection belongs
     */
    public AbstractClientConnection(AbstractServer server) {
        this.server = server;
    }
    
    @Override
    public void onConnect() throws NetworkException {
        super.onConnect();
        
        server.onConnect(this);
    }
    
    @Override
    public void onDisconnect() {
        super.onDisconnect();
        
        server.onDisconnect(this);
    }
    
    @Override
    public IServer getServer() {
        return server;
    }
}
