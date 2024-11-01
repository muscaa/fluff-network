package fluff.network.server;

public class TimeoutConnection {
	
	private final IClientConnection client;
	private final long connectionTime;
	
	public TimeoutConnection(IClientConnection client, long connectionTime) {
		this.client = client;
		this.connectionTime = connectionTime;
	}
	
	public IClientConnection getClient() {
		return client;
	}
	
	public long getConnectionTime() {
		return connectionTime;
	}
}
