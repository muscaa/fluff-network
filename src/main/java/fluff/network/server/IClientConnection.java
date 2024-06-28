package fluff.network.server;

import java.util.UUID;

import fluff.network.client.IClient;

/**
 * Represents a connection to a client on the server side.
 */
public interface IClientConnection extends IClient {
    
    /**
     * Gets the server to which this client is connected.
     * 
     * @return the server instance
     */
    IServer getServer();
    
    /**
     * Gets the unique identifier for this client connection.
     * 
     * @return the UUID of the client connection
     */
    UUID getUUID();
}
