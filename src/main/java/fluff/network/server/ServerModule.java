package fluff.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import fluff.network.NetworkException;
import fluff.network.packet.IPacketOutbound;

/**
 *  A modular hook system for extending and managing internal server functionalities and events.
 */
public abstract class ServerModule {
	
	protected AbstractServer server;
	protected ServerAccess access;
	
	/**
	 * Initializes this server module.
	 * 
	 * @param server the server
	 * @param access the server access
	 * @return true if the module initialized successfully, false otherwise
	 */
	protected boolean init(AbstractServer server, ServerAccess access) {
		this.server = server;
		this.access = access;
		return true;
	}
	
	/**
	 * Called in {@link AbstractServer#onError(Exception)}
	 * 
	 * @param e the error
	 */
	public void onError(Exception e) {}
	
	/**
	 * Called in {@link AbstractServer#onConnect(AbstractClientConnection)}
	 * 
	 * @param connection the client connection
	 * @return true to cancel everything after this call, false otherwise
	 * @throws NetworkException if a network exception occurs
	 */
	public boolean onPreConnect(AbstractClientConnection connection) throws NetworkException {
		return false;
	}
	
	/**
	 * Called in {@link AbstractServer#onConnect(AbstractClientConnection)}
	 * 
	 * @param connection the client connection
	 * @throws NetworkException if a network exception occurs
	 */
	public void onPostConnect(AbstractClientConnection connection) throws NetworkException {}
	
	/**
	 * Called in {@link AbstractServer#onDisconnect(AbstractClientConnection)}
	 * 
	 * @param connection the client connection
	 */
	public void onPreDisconnect(AbstractClientConnection connection) {}
	
	/**
	 * Called in {@link AbstractServer#onDisconnect(AbstractClientConnection)}
	 * 
	 * @param connection the client connection
	 */
	public void onPostDisconnect(AbstractClientConnection connection) {}
	
	/**
	 * Called in {@link AbstractServer#loop()}
	 * 
	 * @param socket the accepted socket
	 * @return true to cancel everything after this call, false otherwise
	 * @throws IOException if an io exception occurs
	 */
	public boolean onPreOpenConnection(Socket socket) throws IOException {
		return false;
	}
	
	/**
	 * Called in {@link AbstractServer#loop()}
	 * 
	 * @param socket the accepted socket
	 * @param connection the client connection
	 * @throws IOException if an io exception occurs
	 * @throws NetworkException if a network exception occurs
	 */
	public void onOpenConnection(Socket socket, AbstractClientConnection connection) throws IOException, NetworkException {}
	
	/**
	 * Called in {@link AbstractServer#loop()}
	 * 
	 * @param connection the client connection
	 * @throws IOException if an io exception occurs
	 * @throws NetworkException if a network exception occurs
	 */
	public void onPostOpenConnection(AbstractClientConnection connection) throws IOException, NetworkException {}
	
	/**
	 * Called in {@link AbstractServer#sendAll(IPacketOutbound)}
	 * 
	 * @param packet the packet
	 * @return true to cancel everything after this call, false otherwise
	 */
	public boolean onPreSendAll(IPacketOutbound packet) {
		return false;
	}
	
	/**
	 * Called in {@link AbstractServer#sendAll(IPacketOutbound)}
	 * 
	 * @param packet the packet
	 */
	public void onSendAll(IPacketOutbound packet) {}
	
	/**
	 * Called in {@link AbstractServer#sendAll(IPacketOutbound)}
	 * 
	 * @param packet the packet
	 */
	public void onPostSendAll(IPacketOutbound packet) {}
	
	/**
	 * Called in {@link AbstractServer#disconnectAll()}
	 * 
	 * @return true to cancel everything after this call, false otherwise
	 */
	public boolean onPreDisconnectAll() {
		return false;
	}
	
	/**
	 * Called in {@link AbstractServer#disconnectAll()}
	 */
	public void onDisconnectAll() {}
	
	/**
	 * Called in {@link AbstractServer#disconnectAll()}
	 */
	public void onPostDisconnectAll() {}
	
	/**
	 * Called in {@link AbstractServer#start(boolean)}
	 * 
	 * @param async if true, the server will start asynchronously
	 */
	public void onPreStart(boolean async) {}
	
	/**
	 * Called in {@link AbstractServer#start(boolean)}
	 * 
	 * @param serverSocket the server socket
	 * @param async if true, the server will start asynchronously
	 */
	public void onStart(ServerSocket serverSocket, boolean async) {}
	
	/**
	 * Called in {@link AbstractServer#start(boolean)}
	 * 
	 * @param async if true, the server will start asynchronously
	 */
	public void onPostStart(boolean async) {}
	
	/**
	 * Called in {@link AbstractServer#stop()}
	 */
	public void onPreStop() {}
	
	/**
	 * Called in {@link AbstractServer#stop()}
	 */
	public void onStop() {}
	
	/**
	 * Called in {@link AbstractServer#stop()}
	 */
	public void onPostStop() {}
}
