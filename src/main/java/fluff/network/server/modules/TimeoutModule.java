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
    		
    		if (tc == null || (tc.connection.getUUID() == null && System.currentTimeMillis() < tc.connectionTime + timeoutDelay)) {
        		try {
    				Thread.sleep(sleepDelay);
    			} catch (InterruptedException e) {}
        		continue;
    		}
    		
    		synchronized (pending) {
    			pending.poll();
			}
    		
    		AbstractClientConnection connection = tc.connection;
    		if (connection.getUUID() != null) {
        		try {
        			connection.onConnect();
        			continue;
        		} catch (NetworkException e) {}
    		}
    		
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
    
    private class TimeoutConnection {
    	
    	private final AbstractClientConnection connection;
    	private final long connectionTime;
    	
    	public TimeoutConnection(AbstractClientConnection connection, long connectionTime) {
    		this.connection = connection;
    		this.connectionTime = connectionTime;
    	}
    }
}
