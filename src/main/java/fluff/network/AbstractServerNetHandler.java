package fluff.network;

import fluff.network.client.IClient;
import fluff.network.server.IClientConnection;
import fluff.network.server.IServer;

/**
 * Abstract implementation of the {@link INetHandler} interface for handling server-side network events and packets.
 * This class provides basic handling for client connection and disconnection on the server side.
 *
 * @param <S> the type of server handled by this net handler
 * @param <C> the type of client connection handled by this net handler
 */
public abstract class AbstractServerNetHandler<S extends IServer, C extends IClientConnection> implements INetHandler {
    
    /**
     * The server associated with this net handler.
     */
    protected S server;
    
    /**
     * The client connection associated with this net handler.
     */
    protected C connection;
    
    @Override
    public void onInit(IClient client) {
        this.connection = (C) client;
        this.server = (S) connection.getServer();
    }
    
    @Override
    public void onConnect() throws NetworkException {}
    
    @Override
    public void onDisconnect() {}
}
