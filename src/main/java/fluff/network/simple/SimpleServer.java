package fluff.network.simple;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import fluff.network.NetworkException;
import fluff.network.server.AbstractServer;

public class SimpleServer extends AbstractServer {
	
	public SimpleServer(int port) {
		super(port);
	}
	
	@Override
	protected void createConnection(Socket socket) throws IOException, NetworkException {
		SimpleClientConnection client = new SimpleClientConnection(this, UUID.randomUUID(), socket, defaultContext, defaultHandlerFunc.invoke());
		
		if (!client.isConnected()) client.disconnect();
	}
}
