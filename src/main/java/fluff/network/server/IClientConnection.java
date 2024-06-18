package fluff.network.server;

import java.util.UUID;

import fluff.network.client.IClient;

public interface IClientConnection extends IClient {
	
	IServer getServer();
	
	UUID getUUID();
}
