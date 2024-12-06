package fluff.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fluff.functions.gen.Func;
import fluff.network.INetHandler;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;
import fluff.network.packet.IPacketOutbound;
import fluff.network.packet.PacketContext;

/**
 * An abstract implementation of the {@link IServer} interface that provides basic functionality for a server.
 */
public abstract class AbstractServer implements IServer {
    
    protected final Map<UUID, AbstractClientConnection> connections = new HashMap<>();
    protected final int port;
    
    protected final List<ServerModule> modules = new LinkedList<>();
	protected final ServerAccess access = new ServerAccess(this);
    
    protected ServerSocket serverSocket;
    
    protected PacketContext<?> defaultContext;
    protected Func<? extends INetHandler> defaultHandlerFunc;
    protected Func<? extends IPacketChannel> defaultChannelFunc;
    
    /**
     * Constructs a new server with the specified port.
     * 
     * @param port the port on which the server will listen for connections
     */
    public AbstractServer(int port) {
        this.port = port;
    }
    
    /**
     * Adds a module to the list of server modules.
     * 
     * @param module the module
     * @return true if the module was added successfully, false otherwise
     */
    protected boolean addModule(ServerModule module) {
    	if (isRunning()) return false;
    	
    	if (!module.init(this, access)) return false;
    	
    	modules.add(module);
    	
    	return true;
    }
    
    /**
     * Creates a new client connection.
     * 
     * @return a new client connection
     */
    protected abstract AbstractClientConnection createConnection();
    
    /**
     * Sets the default context and handler function for the server.
     * 
     * @param defaultContext the default packet context
     * @param defaultHandlerFunc the function to create the default handler
     */
    protected void setDefaultContextUnsafe(PacketContext<?> defaultContext, Func<? extends INetHandler> defaultHandlerFunc) {
        this.defaultContext = defaultContext;
        this.defaultHandlerFunc = defaultHandlerFunc;
    }
    
    /**
     * Called when an error occurs.
     * 
     * @param e the exception representing the error
     */
    protected void onError(Exception e) {
    	for (ServerModule m : modules) {
    		m.onError(e);
    	}
    }
    
    /**
     * Called when a client connects to the server.
     * 
     * @param connection the client connection
     * @throws NetworkException if a network-related error occurs
     */
    protected void onConnect(AbstractClientConnection connection) throws NetworkException {
    	boolean cancel = false;
    	for (ServerModule m : modules) {
    		cancel |= m.onPreConnect(connection);
    	}
    	if (cancel) return;
    	
    	UUID uuid = connection.getUUID();
    	if (uuid == null) throw new NetworkException("Client's UUID cannot be null!");
        if (connections.containsKey(uuid)) throw new NetworkException("Client with UUID " + connection.getUUID() + " already exists!");
        
        synchronized (connections) {
        	connections.put(uuid, connection);
		}
        
    	for (ServerModule m : modules) {
    		m.onPostConnect(connection);
    	}
    }
    
    /**
     * Called when a client disconnects from the server.
     * 
     * @param connection the client connection
     */
    protected void onDisconnect(AbstractClientConnection connection) {
    	for (ServerModule m : modules) {
    		m.onPreDisconnect(connection);
    	}
    	
    	UUID uuid = connection.getUUID();
    	if (uuid == null) return;
    	
        synchronized (connections) {
        	connections.remove(uuid);
		}
        
    	for (ServerModule m : modules) {
    		m.onPostDisconnect(connection);
    	}
    }
    
    /**
     * The main server loop that waits for client connections.
     */
    @SuppressWarnings("resource")
	protected void loop() {
        while (isRunning()) {
        	Socket socket = null;
            try {
            	socket = serverSocket.accept();
            	
            	boolean cancel = false;
            	for (ServerModule m : modules) {
            		cancel |= m.onPreOpenConnection(socket);
            	}
            	if (cancel) continue; // warning: socket remains open
                
                AbstractClientConnection connection = createConnection();
                
            	for (ServerModule m : modules) {
            		m.onOpenConnection(socket, connection);
            	}
                
                connection.openConnection(socket);
                
            	for (ServerModule m : modules) {
            		m.onPostOpenConnection(connection);
            	}
            } catch (IOException | NetworkException e) {
                onError(e);
                
                try {
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e1) {
					onError(e1);
				}
            }
        }
    }
    
    /**
     * Gets the current connections UUID key set. Used to avoid synchronization errors.
     * 
     * @return the current connections UUID key set
     */
    protected Set<UUID> getUUIDKeys() {
    	Set<UUID> set;
    	synchronized (connections) {
    		set = Set.copyOf(connections.keySet());
		}
    	return set;
    }
    
    @Override
    public void sendAll(IPacketOutbound packet) {
    	boolean cancel = false;
    	for (ServerModule m : modules) {
    		cancel |= m.onPreSendAll(packet);
    	}
    	if (cancel) return;
    	
    	for (ServerModule m : modules) {
    		m.onSendAll(packet);
    	}
    	
    	Set<UUID> keys = getUUIDKeys();
        for (UUID uuid : keys) {
        	AbstractClientConnection connection = connections.get(uuid);
        	if (connection == null) continue;
        	
            connection.send(packet);
        }
        
    	for (ServerModule m : modules) {
    		m.onPostSendAll(packet);
    	}
    }
    
    @Override
    public void disconnectAll() {
    	boolean cancel = false;
    	for (ServerModule m : modules) {
    		cancel |= m.onPreDisconnectAll();
    	}
    	if (cancel) return;
    	
    	for (ServerModule m : modules) {
    		m.onDisconnectAll();
    	}
    	
    	Set<UUID> keys = getUUIDKeys();
        for (UUID uuid : keys) {
        	AbstractClientConnection connection = connections.get(uuid);
        	if (connection == null) continue;
        	
            connection.disconnect();
        }
        
    	for (ServerModule m : modules) {
    		m.onPostDisconnectAll();
    	}
    }
    
    @Override
    public void start(boolean async) throws NetworkException {
        if (isRunning()) throw new NetworkException("Server already running!");
        
    	for (ServerModule m : modules) {
    		m.onPreStart(async);
    	}
        
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new NetworkException(e);
        }
        
    	for (ServerModule m : modules) {
    		m.onStart(serverSocket, async);
    	}
        
        if (async) {
            Thread t = new Thread(this::loop);
            t.setName("Server Loop");
            t.setDaemon(true);
            t.start();
        } else {
            loop();
        }
        
    	for (ServerModule m : modules) {
    		m.onPostStart(async);
    	}
    }
    
    @Override
    public void stop() {
        if (!isRunning()) return;
        
    	for (ServerModule m : modules) {
    		m.onPreStop();
    	}
        
        disconnectAll();
        
    	for (ServerModule m : modules) {
    		m.onStop();
    	}
        
        try {
            serverSocket.close();
        } catch (IOException e) {}
        
    	for (ServerModule m : modules) {
    		m.onPostStop();
    	}
    }
    
    @Override
    public boolean isRunning() {
        return serverSocket != null && !serverSocket.isClosed();
    }
    
    @Override
    public <V extends INetHandler> void setDefaultContext(PacketContext<? super V> defaultContext, Func<V> defaultHandlerFunc) {
        setDefaultContextUnsafe(defaultContext, defaultHandlerFunc);
    }
    
    @Override
    public void setDefaultChannel(Func<? extends IPacketChannel> defaultChannelFunc) {
        this.defaultChannelFunc = defaultChannelFunc;
    }
}
