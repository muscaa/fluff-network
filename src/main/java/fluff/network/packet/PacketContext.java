package fluff.network.packet;

import java.util.HashMap;
import java.util.Map;

import fluff.functions.gen.Func;
import fluff.functions.gen.obj.obj.VoidFunc2;
import fluff.network.INetHandler;

/**
 * Represents a context for managing network packet descriptors. Provides functionality to register, unregister,
 * and retrieve packet descriptors and their associated IDs.
 *
 * @param <V> the type of network handler
 */
public class PacketContext<V extends INetHandler> {
    
	protected final Map<Integer, PacketDescriptor> descriptors = new HashMap<>();
    protected final Map<Class<? extends IPacketBase>, Integer> ids = new HashMap<>();
    
    protected final String name;
    
    /**
     * Constructs a new PacketContext with the specified name.
     *
     * @param name the name of the packet context
     */
    public PacketContext(String name) {
        this.name = name;
    }
    
    /**
     * Constructs a new PacketContext with the specified common context.
     *
     * @param commonContext the common context of the packet context
     */
    public PacketContext(PacketContext<? super V> commonContext) {
        this(commonContext.getName());
        
        extend(commonContext);
    }
    
    /**
     * Extends the current packet context with another packet context, inheriting all packet descriptors and IDs.
     *
     * @param context the packet context to extend from
     * @return the extended packet context
     */
    public PacketContext<V> extend(PacketContext<? super V> context) {
        for (Map.Entry<Integer, PacketDescriptor> e : context.descriptors.entrySet()) {
        	register(e.getKey(), e.getValue());
        }
        return this;
    }
    
    protected <P extends IPacketBase> PacketContext<V> register(int id, PacketDescriptor descriptor) {
        descriptors.put(id, descriptor);
        if (descriptor.getPacketClass() != null) ids.put(descriptor.getPacketClass(), id);
        return this;
    }
    
    /**
     * Registers a new packet descriptor with the specified ID, packet class, creation function, and handling function.
     *
     * @param <P> the type of the packet
     * @param id the ID of the packet
     * @param packetClass the class of the packet
     * @param packetFunc the function to create a new packet instance
     * @param handleFunc the function to handle the packet
     * @return the packet context with the new registration
     */
    public <P extends IPacket> PacketContext<V> register(int id, Class<P> packetClass, Func<P> packetFunc, VoidFunc2<V, P> handleFunc) {
        return register(id, new PacketDescriptor<>(PacketDirection.BOTH, packetClass, packetFunc, handleFunc));
    }
    
    /**
     * Registers a new inbound packet descriptor with the specified ID, creation function, and handling function.
     *
     * @param <P> the type of the inbound packet
     * @param id the ID of the inbound packet
     * @param packetFunc the function to create a new inbound packet instance
     * @param handleFunc the function to handle the inbound packet
     * @return the inbound packet context with the new registration
     */
    public <P extends IPacketInbound> PacketContext<V> registerInbound(int id, Func<P> packetFunc, VoidFunc2<V, P> handleFunc) {
        return register(id, new PacketDescriptor<>(PacketDirection.INBOUND, null, packetFunc, handleFunc));
    }
    
    /**
     * Registers a new outbound packet descriptor with the specified ID and outbound packet class.
     *
     * @param <P> the type of the outbound packet
     * @param id the ID of the outbound packet
     * @param packetClass the class of the outbound packet
     * @return the outbound packet context with the new registration
     */
    public <P extends IPacketOutbound> PacketContext<V> registerOutbound(int id, Class<P> packetClass) {
        return register(id, new PacketDescriptor<>(PacketDirection.OUTBOUND, packetClass, null, null));
    }
    
    /**
     * Unregisters a packet descriptor with the specified ID.
     *
     * @param id the ID of the packet descriptor to unregister
     * @return the packet context with the descriptor removed
     */
    public PacketContext<V> unregister(int id) {
        PacketDescriptor descriptor = descriptors.remove(id);
        if (descriptor.getPacketClass() != null) ids.remove(descriptor.getPacketClass());
        return this;
    }
    
    /**
     * Checks if the packet context contains a descriptor with the specified ID.
     *
     * @param id the ID to check
     * @return true if the context contains the descriptor, false otherwise
     */
    public boolean contains(int id) {
        return descriptors.containsKey(id);
    }
    
    /**
     * Checks if the packet context contains a descriptor for the specified packet class.
     *
     * @param packetClass the packet class to check
     * @return true if the context contains the descriptor, false otherwise
     */
    public boolean contains(Class<? extends IPacketBase> packetClass) {
        return ids.containsKey(packetClass);
    }
    
    /**
     * Retrieves the packet descriptor with the specified ID.
     *
     * @param id the ID of the packet descriptor
     * @return the packet descriptor, or null if not found
     */
    public PacketDescriptor getDescriptor(int id) {
        return descriptors.get(id);
    }
    
    /**
     * Retrieves the ID associated with the specified packet class.
     *
     * @param packetClass the packet class
     * @return the ID associated with the packet class, or null if not found
     */
    public int getID(Class<? extends IPacketBase> packetClass) {
        return ids.get(packetClass);
    }
    
    /**
     * Retrieves the name of the packet context.
     *
     * @return the name of the packet context
     */
    public String getName() {
        return name;
    }
}
