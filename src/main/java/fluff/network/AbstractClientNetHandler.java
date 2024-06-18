package fluff.network;

import fluff.network.client.IClient;

public abstract class AbstractClientNetHandler<C extends IClient> implements INetHandler {
	
	protected C client;
	
	@Override
	public void onConnect(IClient client) throws NetworkException {
		this.client = (C) client;
	}
	
	@Override
	public void onDisconnect() {
	}
}
