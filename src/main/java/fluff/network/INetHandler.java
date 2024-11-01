package fluff.network;

import fluff.network.client.IClient;

/**
 * Interface for handling network events and packets.
 */
public interface INetHandler {
    
    /**
     * Called when this handler is assigned to a client.
     *
     * @param client the client
     */
    void onInit(IClient client);
    
    /**
     * Called when a client connects to the network. If the initial handler was
     * changed, this method may not be called.
     *
     * @throws NetworkException if an error occurs during the connection handling
     */
    void onConnect() throws NetworkException;
    
    /**
     * Called when a client disconnects from the network.
     */
    void onDisconnect();
}
