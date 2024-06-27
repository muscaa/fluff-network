package fluff.network.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

import fluff.bin.stream.BinaryInputStream;
import fluff.bin.stream.BinaryOutputStream;
import fluff.network.INetHandler;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketBase;
import fluff.network.packet.IPacketChannel;
import fluff.network.packet.IPacketInbound;
import fluff.network.packet.IPacketOutbound;
import fluff.network.packet.PacketContext;
import fluff.network.packet.PacketDescriptor;

public abstract class AbstractClient implements IClient {
	
	protected Socket socket;
	protected InputStream socketIn;
	protected OutputStream socketOut;
	
	protected PacketContext<?> context;
	protected INetHandler handler;
	protected IPacketChannel channel;
	
	protected void openConnection(Socket socket) throws IOException, NetworkException {
		if (isConnected()) throw new NetworkException("Client already has a connection!");
		
		this.socket = socket;
		this.socketIn = socket.getInputStream();
		this.socketOut = socket.getOutputStream();
		
		Thread t = new Thread(this::handleReceive);
		t.setName("Packet Receiver");
		t.setDaemon(true);
		t.start();
		
		onConnect();
	}
	
	protected void closeConnection() {
		if (!isConnected()) return;
		
		onDisconnect();
		
		try {
			socketIn.close();
		} catch (IOException e) {}
		try {
			socketOut.close();
		} catch (IOException e) {}
		try {
			socket.close();
		} catch (IOException e) {}
	}
	
	protected void onError(ClientErrorType type, Exception e) {
		switch (type) {
			case CONNECTION:
				disconnect();
				break;
			case READ:
				disconnect();
				break;
			case WRITE:
				// nothing
				break;
		}
	}
	
	protected void onConnect() throws NetworkException {
		handler.onConnect(this);
	}
	
	protected void onDisconnect() {
		handler.onDisconnect();
	}
	
	protected void handleSend(IPacketOutbound packet) throws SocketException, IOException, NetworkException {
		Class<? extends IPacketOutbound> packetClass = packet.getClass();
		if (!context.contains(packetClass)) throw new NetworkException("Invalid packet!");
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream out = new BinaryOutputStream(bytes);
		
		out.Int(context.getID(packetClass));
		out.Data(packet);
		
		channel.write(socketOut, bytes);
	}
	
	protected void handleReceive() {
		while (isConnected()) {
			try {
				ByteArrayInputStream bytes = channel.read(socketIn);
				BinaryInputStream in = new BinaryInputStream(bytes);
				
				int id = in.Int();
				if (!context.contains(id)) throw new NetworkException("Packet does not exist!");
				
				PacketDescriptor descriptor = context.getDescriptor(id);
				IPacketBase packetBase = descriptor.create();
				if (!(packetBase instanceof IPacketInbound packet)) throw new NetworkException("Received outbound packet!");
				
				in.Data(packet);
				
				receive(descriptor, packet);
			} catch (SocketException e) {
				onError(ClientErrorType.CONNECTION, e);
			} catch (IOException | NetworkException e) {
				onError(ClientErrorType.READ, e);
			}
		}
	}
	
	protected void receive(PacketDescriptor descriptor, IPacketInbound packet) {
		Objects.requireNonNull(handler);
		Objects.requireNonNull(descriptor);
		Objects.requireNonNull(packet);
		
		descriptor.handle(handler, packet);
	}
	
	@Override
	public void send(IPacketOutbound packet) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(channel);
		Objects.requireNonNull(packet);
		
		try {
			handleSend(packet);
		} catch (SocketException e) {
			onError(ClientErrorType.CONNECTION, e);
		} catch (IOException | NetworkException e) {
			onError(ClientErrorType.WRITE, e);
		}
	}
	
	@Override
	public void disconnect() {
		closeConnection();
	}
	
	@Override
	public boolean isConnected() {
		return socket != null && socket.isConnected() && !socket.isClosed();
	}
	
	protected void setContextUnsafe(PacketContext<?> context, INetHandler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	@Override
	public <V extends INetHandler> void setContext(PacketContext<? super V> context, V handler) {
		setContextUnsafe(context, handler);
	}
	
	@Override
	public void setChannel(IPacketChannel channel) {
		this.channel = channel;
	}
}
