package fluff.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fluff.functions.gen.Func;
import fluff.network.INetHandler;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;
import fluff.network.packet.IPacketOutbound;
import fluff.network.packet.PacketContext;

public abstract class AbstractServer implements IServer {
	
	protected final Map<UUID, IClientConnection> connections = new HashMap<>();
	
	protected final int port;
	
	protected ServerSocket serverSocket;
	
	protected PacketContext<?> defaultContext;
	protected Func<? extends INetHandler> defaultHandlerFunc;
	protected Func<? extends IPacketChannel> defaultChannelFunc;
	
	public AbstractServer(int port) {
		this.port = port;
	}
	
	protected abstract void createConnection(Socket socket) throws IOException, NetworkException;
	
	protected void setDefaultContextUnsafe(PacketContext<?> defaultContext, Func<? extends INetHandler> defaultHandlerFunc) {
		this.defaultContext = defaultContext;
		this.defaultHandlerFunc = defaultHandlerFunc;
	}
	
	protected void onError(Exception e) {}
	
	protected void onConnect(IClientConnection client) throws NetworkException {
		if (connections.containsKey(client.getUUID())) throw new NetworkException("Client with UUID " + client.getUUID() + " already exists!");
		
		connections.put(client.getUUID(), client);
	}
	
	protected void onDisconnect(IClientConnection client) {
		connections.remove(client.getUUID());
	}
	
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
	
	@Override
	public void sendAll(IPacketOutbound packet) {
		for (Map.Entry<UUID, IClientConnection> e : connections.entrySet()) {
			e.getValue().send(packet);
		}
	}
	
	@Override
	public void disconnectAll() {
		for (Map.Entry<UUID, IClientConnection> e : connections.entrySet()) {
			e.getValue().disconnect();
		}
	}
	
	@Override
	public void start(boolean async) throws NetworkException {
		if (isRunning()) throw new NetworkException("Server already running!");
		
		try {
			serverSocket = new ServerSocket(port);
			// new ServerSocket(port, 0, InetAddress.getLoopbackAddress());
		} catch (IOException e) {
			throw new NetworkException(e);
		}
		
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
