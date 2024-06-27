package fluff.network.simple;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import fluff.network.INetHandler;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;
import fluff.network.packet.PacketContext;
import fluff.network.server.AbstractClientConnection;
import fluff.network.server.AbstractServer;

public class SimpleClientConnection extends AbstractClientConnection {
	
	private final UUID uuid;
	
	public SimpleClientConnection(AbstractServer server, UUID uuid, Socket socket, PacketContext<?> context, INetHandler handler, IPacketChannel channel) throws IOException, NetworkException {
		super(server);
		
		this.uuid = uuid;
		
		setContextUnsafe(context, handler);
		setChannel(channel);
		openConnection(socket);
	}
	
	@Override
	public UUID getUUID() {
		return uuid;
	}
}
