package fluff.network.packet;

import fluff.functions.gen.Func;
import fluff.functions.gen.obj.obj.VoidFunc2;
import fluff.network.INetHandler;

public class PacketDescriptor<H extends INetHandler, P extends IPacketBase> {
	
	private final Class<P> packetClass;
	private final Func<P> packetFunc;
	private final VoidFunc2<H, P> handleFunc;
	
	public PacketDescriptor(Class<P> packetClass, Func<P> packetFunc, VoidFunc2<H, P> handleFunc) {
		this.packetClass = packetClass;
		this.packetFunc = packetFunc;
		this.handleFunc = handleFunc;
	}
	
	public IPacketBase create() {
		return packetFunc.invoke();
	}
	
	public void handle(H handler, P packet) {
		handleFunc.invoke(handler, packet);
	}
	
	public Class<? extends IPacketBase> getPacketClass() {
		return packetClass;
	}
}
