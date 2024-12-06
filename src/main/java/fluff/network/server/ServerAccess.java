package fluff.network.server;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fluff.network.NetworkException;

/**
 * Provides internal server access for more functionality.
 */
public class ServerAccess {
	
	private final AbstractServer server;
	
	/**
	 * Constructs a new ServerAccess instance with the specified server.
	 * 
	 * @param server the server
	 */
	public ServerAccess(AbstractServer server) {
		this.server = server;
	}
	
	/**
	 * Returns the server used for internal access.
	 * 
	 * @return the server used
	 */
	public AbstractServer getServer() {
		return server;
	}
	
	/**
	 * Used to get r/w access to the server connections.
	 * 
	 * @return the server connections
	 */
	public Map<UUID, AbstractClientConnection> connections() {
		return server.connections;
	}
	
	/**
	 * Gets the server port.
	 * 
	 * @return the server port
	 */
	public int getPort() {
		return server.port;
	}
	
	/**
	 * Gets the server uuid keys.
	 * 
	 * @return the server uuid keys
	 */
	public Set<UUID> getUUIDKeys() {
		return server.getUUIDKeys();
	}
	
	/**
	 * Calls {@link AbstractServer#onError(Exception)}
	 * 
	 * @param e the exception
	 */
    public void onError(Exception e) {
    	server.onError(e);
    }
    
	/**
	 * Calls {@link AbstractServer#onConnect(AbstractClientConnection)}
	 * 
	 * @param connection the client connection
	 */
    public void onConnect(AbstractClientConnection connection) throws NetworkException {
    	server.onConnect(connection);
    }
    
	/**
	 * Calls {@link AbstractServer#onDisconnect(AbstractClientConnection)}
	 * 
	 * @param connection the client connection
	 */
    public void onDisconnect(AbstractClientConnection connection) {
    	server.onDisconnect(connection);
    }
}
