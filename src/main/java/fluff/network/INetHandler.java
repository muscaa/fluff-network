package fluff.network;

import fluff.network.client.IClient;

/**
 * Interface for handling network events and packets.
 */
public interface INetHandler {
	
    /**
     * Called when a client connects to the network.
     *
     * @param client the client that has connected
     * @throws NetworkException if an error occurs during the connection handling
     */
    void onConnect(IClient client) throws NetworkException;
    
    /**
     * Called when a client disconnects from the network.
     */
    void onDisconnect();
}
