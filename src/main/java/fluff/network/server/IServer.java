package fluff.network.server;

import fluff.functions.gen.Func;
import fluff.network.INetHandler;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;
import fluff.network.packet.IPacketOutbound;
import fluff.network.packet.PacketContext;

public interface IServer {
	
	void sendAll(IPacketOutbound packet);
	
	void disconnectAll();
	
	void start(boolean async) throws NetworkException;
	
	void stop();
	
	boolean isRunning();
	
	<V extends INetHandler> void setDefaultContext(PacketContext<? super V> defaultContext, Func<V> defaultHandlerFunc);
	
	void setDefaultChannel(Func<? extends IPacketChannel> defaultChannelFunc);
}
