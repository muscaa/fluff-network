package fluff.network.server;

import fluff.network.NetworkException;
import fluff.network.client.AbstractClient;

public abstract class AbstractClientConnection extends AbstractClient implements IClientConnection {
	
	protected final AbstractServer server;
	
	public AbstractClientConnection(AbstractServer server) {
		this.server = server;
	}
	
	@Override
	protected void onConnect() throws NetworkException {
		super.onConnect();
		
		server.onConnect(this);
	}
	
	@Override
	protected void onDisconnect() {
		super.onDisconnect();
		
		server.onDisconnect(this);
	}
	
	@Override
	public IServer getServer() {
		return server;
	}
}
