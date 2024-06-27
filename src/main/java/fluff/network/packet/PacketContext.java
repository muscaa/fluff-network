package fluff.network.packet;

import java.util.HashMap;
import java.util.Map;

import fluff.functions.gen.Func;
import fluff.functions.gen.obj.obj.VoidFunc2;
import fluff.network.INetHandler;

public class PacketContext<V extends INetHandler> {
	
	private final Map<Integer, PacketDescriptor> descriptors = new HashMap<>();
	private final Map<Class<? extends IPacketBase>, Integer> ids = new HashMap<>();
	
	private final String name;
	
	public PacketContext(String name) {
		this.name = name;
	}
	
	public PacketContext<V> extend(PacketContext<? super V> context) {
		for (Map.Entry<Integer, PacketDescriptor> e : context.descriptors.entrySet()) {
			descriptors.put(e.getKey(), e.getValue());
			ids.put(e.getValue().getPacketClass(), e.getKey());
		}
		return this;
	}
	
	public <P extends IPacketBase> PacketContext<V> register(int id, Class<P> packetClass, Func<P> packetFunc, VoidFunc2<V, P> handleFunc) {
		descriptors.put(id, new PacketDescriptor<>(packetClass, packetFunc, handleFunc));
		ids.put(packetClass, id);
		return this;
	}
	
	public PacketContext<V> unregister(int id) {
		PacketDescriptor descriptor = descriptors.remove(id);
		ids.remove(descriptor.getPacketClass());
		return this;
	}
	
	public boolean contains(int id) {
		return descriptors.containsKey(id);
	}
	
	public boolean contains(Class<? extends IPacketBase> packetClass) {
		return ids.containsKey(packetClass);
	}
	
	public PacketDescriptor getDescriptor(int id) {
		return descriptors.get(id);
	}
	
	public int getID(Class<? extends IPacketBase> packetClass) {
		return ids.get(packetClass);
	}
	
	public String getName() {
		return name;
	}
}
