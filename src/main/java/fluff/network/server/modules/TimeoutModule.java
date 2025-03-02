package fluff.network.server.modules;

import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import fluff.network.NetworkException;
import fluff.network.server.AbstractClientConnection;
import fluff.network.server.ServerModule;

/**
 * Represents a server module that adds unregistered clients to a queue and wait for their registration.
 */
public class TimeoutModule extends ServerModule {
	
	public static final long DEFAULT_TIMEOUT_DELAY = 3000;
	public static final long DEFAULT_SLEEP_DELAY = 500;
	
	protected final Queue<TimeoutConnection> pending = new LinkedList<>();
	
    protected final long timeoutDelay;
    protected final long sleepDelay;
    
    /**
     * Constructs a new timeout module with the specified timeout delay and sleep delay.
     * 
     * @param timeoutDelay the timeout delay before disconnecting the client
     * @param sleepDelay the sleep delay between checks
     */
    public TimeoutModule(long timeoutDelay, long sleepDelay) {
        this.timeoutDelay = timeoutDelay;
        this.sleepDelay = sleepDelay;
    }
    
    /**
     * Constructs a new timeout module with the specified timeout delay.
     * 
     * @param timeoutDelay the timeout delay before disconnecting the client
     */
    public TimeoutModule(long timeoutDelay) {
        this(timeoutDelay, DEFAULT_SLEEP_DELAY);
    }
    
    /**
     * Constructs a new timeout module with default delays.
     */
    public TimeoutModule() {
    	this(DEFAULT_TIMEOUT_DELAY);
	}
	
    /**
     * The timeout loop that waits for client UUIDs.
     */
    protected void loop() {
    	while (server.isRunning()) {
    		TimeoutConnection tc = pending.peek();
    		
    		if (tc == null || (tc.connection.isConnected() && tc.connection.getUUID() == null && System.currentTimeMillis() < tc.connectionTime + timeoutDelay)) {
        		try {
    				Thread.sleep(sleepDelay);
    			} catch (InterruptedException e) {}
        		continue;
    		}
    		
    		synchronized (pending) {
    			pending.poll();
			}
    		
    		AbstractClientConnection connection = tc.connection;
			if (!connection.isConnected()) continue;
    		
    		if (connection.getUUID() != null) {
        		try {
        			connect(connection);
        			continue;
        		} catch (NetworkException e) {}
    		}
    		
    		disconnect(connection);
    	}
    }
    
    /**
     * Connects the client to the server.
     * 
     * @param connection the client connection
     * @throws NetworkException if an error occurs while connecting the client
     */
	protected void connect(AbstractClientConnection connection) throws NetworkException {
		if (connection instanceof TimeoutListener listener) {
			listener.onConnectionEstablished();
			
			access.onConnect(connection);
		} else {
			connection.onConnect();
		}
	}
	
	/**
	 * Disconnects the client from the server.
	 * 
	 * @param connection the client connection
	 */
	protected void disconnect(AbstractClientConnection connection) {
		if (connection instanceof TimeoutListener listener) {
			listener.onConnectionTimedOut();
		} else {
			connection.disconnect();
		}
	}
    
    @Override
    public void onStart(ServerSocket serverSocket, boolean async) {
        Thread t = new Thread(this::loop);
        t.setName("Timeout Loop");
        t.setDaemon(true);
        t.start();
    }
    
    @Override
    public boolean onPreConnect(AbstractClientConnection connection) throws NetworkException {
    	UUID uuid = connection.getUUID();
    	if (uuid == null) {
    		synchronized (pending) {
    			pending.offer(new TimeoutConnection(connection, System.currentTimeMillis()));
			}
    		return true;
    	}
    	return false;
    }
    
    @Override
    public void onDisconnectAll() {
        for (TimeoutConnection tc : pending) {
        	tc.connection.disconnect();
        }
        synchronized (pending) {
        	pending.clear();
		}
    }
    
    /**
     * Represents a timeout connection.
     */
    public static class TimeoutConnection {
    	
    	public final AbstractClientConnection connection;
    	public final long connectionTime;
    	
    	public TimeoutConnection(AbstractClientConnection connection, long connectionTime) {
    		this.connection = connection;
    		this.connectionTime = connectionTime;
    	}
    }
    
    /**
     * Represents a timeout listener.
     */
    public static interface TimeoutListener {
    	
    	/**
    	 * Called when the connection is timed out.
    	 */
    	void onConnectionTimedOut();
    	
    	/**
    	 * Called when the connection is established.
    	 * 
    	 * @throws NetworkException if an error occurs while establishing the connection
    	 */
    	void onConnectionEstablished() throws NetworkException;
    }
}
