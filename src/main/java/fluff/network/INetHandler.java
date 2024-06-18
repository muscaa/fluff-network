package fluff.network;

import fluff.network.client.IClient;

public interface INetHandler {
	
	void onConnect(IClient client) throws NetworkException;
	
	void onDisconnect();
}
