package fluff.network;

import fluff.network.client.IClient;

/**
 * Abstract implementation of the {@link INetHandler} interface for handling client-side network events and packets.
 * This class provides basic handling for client connection and disconnection on the client side.
 *
 * @param <C> the type of client handled by this net handler
 */
public abstract class AbstractClientNetHandler<C extends IClient> implements INetHandler {
    
    /**
     * The client associated with this net handler.
     */
    protected C client;
    
    @Override
    public void onInit(IClient client) {
        this.client = (C) client;
    }
    
    @Override
    public void onConnect() throws NetworkException {}
    
    @Override
    public void onDisconnect() {}
}
