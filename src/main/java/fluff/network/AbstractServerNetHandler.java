package fluff.network;

import fluff.network.client.IClient;
import fluff.network.server.IClientConnection;
import fluff.network.server.IServer;

public abstract class AbstractServerNetHandler<S extends IServer, C extends IClientConnection> implements INetHandler {
	
	protected S server;
	protected C connection;
	
	@Override
	public void onConnect(IClient client) throws NetworkException {
		this.connection = (C) client;
		this.server = (S) connection.getServer();
	}
	
	@Override
	public void onDisconnect() {}
}
