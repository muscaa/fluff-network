package fluff.network.client;

import fluff.network.INetHandler;
import fluff.network.packet.IPacketChannel;
import fluff.network.packet.IPacketOutbound;
import fluff.network.packet.PacketContext;

public interface IClient {
	
	void send(IPacketOutbound packet);
	
	void disconnect();
	
	boolean isConnected();
	
	<V extends INetHandler> void setContext(PacketContext<? super V> context, V handler);
	
	void setChannel(IPacketChannel channel);
}
