package fluff.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
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
    
    protected final Map<UUID, IClientConnection> connections = new HashMap<>();
    protected final Queue<TimeoutConnection> pending = new LinkedList<>();
    
    protected final int port;
    protected final long timeoutDelay;
    protected final long sleepDelay;
    
    protected ServerSocket serverSocket;
    
    protected PacketContext<?> defaultContext;
    protected Func<? extends INetHandler> defaultHandlerFunc;
    protected Func<? extends IPacketChannel> defaultChannelFunc;
    
    /**
     * Constructs a new server with the specified port, timeout delay and sleep delay.
     * 
     * @param port the port on which the server will listen for connections
     * @param timeoutDelay the delay before a connection times out
     * @param sleepDelay the delay to sleep between timeout checks
     */
    public AbstractServer(int port, long timeoutDelay, long sleepDelay) {
        this.port = port;
        this.timeoutDelay = timeoutDelay;
        this.sleepDelay = sleepDelay;
    }
    
    /**
     * Constructs a new server with the specified port and timeout delay.
     * 
     * @param port the port on which the server will listen for connections
     * @param timeoutDelay the delay before a connection times out
     */
    public AbstractServer(int port, long timeoutDelay) {
        this(port, timeoutDelay, 500);
    }
    
    /**
     * Constructs a new server with the specified port.
     * 
     * @param port the port on which the server will listen for connections
     */
    public AbstractServer(int port) {
        this(port, 3000);
    }
    
    /**
     * Creates a new client connection for the specified socket.
     * 
     * @param socket the socket representing the client connection
     * @throws IOException if an I/O error occurs when creating the connection
     * @throws NetworkException if a network-related error occurs when creating the connection
     */
    protected abstract void createConnection(Socket socket) throws IOException, NetworkException;
    
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
    protected void onError(Exception e) {}
    
    /**
     * Called when a client connects to the server.
     * 
     * @param client the client connection
     * @throws NetworkException if a network-related error occurs
     */
    protected void onConnect(IClientConnection client) throws NetworkException {
    	UUID uuid = client.getUUID();
    	if (uuid == null) {
    		pending.offer(new TimeoutConnection(client, System.currentTimeMillis()));
    		return;
    	}
    	
        if (connections.containsKey(uuid)) throw new NetworkException("Client with UUID " + client.getUUID() + " already exists!");
        
        connections.put(uuid, client);
    }
    
    /**
     * Called when a client disconnects from the server.
     * 
     * @param client the client connection
     */
    protected void onDisconnect(IClientConnection client) {
    	UUID uuid = client.getUUID();
    	if (uuid == null) return;
    	
        connections.remove(uuid);
    }
    
    /**
     * The main server loop that waits for client connections.
     */
    @SuppressWarnings("resource")
    protected void loop() {
        while (isRunning()) {
            try {
                Socket socket = serverSocket.accept();
                
                createConnection(socket);
            } catch (IOException | NetworkException e) {
                onError(e);
            }
        }
    }
    
    /**
     * The timeout loop that waits for client connections to timeout.
     */
    protected void timeoutLoop() {
    	while (isRunning()) {
    		TimeoutConnection tc = pending.peek();
    		
    		if (tc == null || System.currentTimeMillis() < tc.getConnectionTime() + timeoutDelay) {
        		try {
    				Thread.sleep(sleepDelay);
    			} catch (InterruptedException e) {}
        		continue;
    		}
    		
    		pending.poll();
    		
    		IClientConnection client = tc.getClient();
    		if (!establishConnection(client)) {
    			client.disconnect();
    		}
    	}
    }
    
    protected boolean establishConnection(IClientConnection client) {
		UUID uuid = client.getUUID();
		if (uuid == null) {
			return false;
		}
		
		try {
			client.onConnect();
			return true;
		} catch (NetworkException e) {}
		
		return false;
    }
    
    @Override
    public void sendAll(IPacketOutbound packet) {
        for (Map.Entry<UUID, IClientConnection> e : connections.entrySet()) {
            e.getValue().send(packet);
        }
        // pending connections not included
    }
    
    @Override
    public void disconnectAll() {
        for (Map.Entry<UUID, IClientConnection> e : connections.entrySet()) {
            e.getValue().disconnect();
        }
        for (TimeoutConnection tc : pending) { // might have some synchronization bugs
        	tc.getClient().disconnect();
        }
        pending.clear();
    }
    
    @Override
    public void start(boolean async) throws NetworkException {
        if (isRunning()) throw new NetworkException("Server already running!");
        
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new NetworkException(e);
        }
        
        Thread timeoutThread = new Thread(this::timeoutLoop);
        timeoutThread.setName("Timeout Loop");
        timeoutThread.setDaemon(true);
        timeoutThread.start();
        
        if (async) {
            Thread t = new Thread(this::loop);
            t.setName("Server Loop");
            t.setDaemon(true);
            t.start();
        } else {
            loop();
        }
    }
    
    @Override
    public void stop() {
        if (!isRunning()) return;
        
        disconnectAll();
        
        try {
            serverSocket.close();
        } catch (IOException e) {}
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
