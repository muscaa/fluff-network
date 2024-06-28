package fluff.network.packet;

import fluff.functions.gen.Func;
import fluff.functions.gen.obj.obj.VoidFunc2;
import fluff.network.INetHandler;

/**
 * Represents a descriptor for a network packet, associating a packet class, a packet creation function,
 * and a packet handling function with a specific network handler.
 *
 * @param <H> the type of network handler
 * @param <P> the type of packet
 */
public class PacketDescriptor<H extends INetHandler, P extends IPacketBase> {
	
    private final Class<P> packetClass;
    private final Func<P> packetFunc;
    private final VoidFunc2<H, P> handleFunc;
    
    /**
     * Constructs a new PacketDescriptor with the specified packet class, creation function, and handling function.
     *
     * @param packetClass the class of the packet
     * @param packetFunc the function to create a new packet instance
     * @param handleFunc the function to handle the packet
     */
    public PacketDescriptor(Class<P> packetClass, Func<P> packetFunc, VoidFunc2<H, P> handleFunc) {
        this.packetClass = packetClass;
        this.packetFunc = packetFunc;
        this.handleFunc = handleFunc;
    }
    
    /**
     * Creates a new packet instance using the creation function.
     *
     * @return a new packet instance
     */
    public IPacketBase create() {
        return packetFunc.invoke();
    }
    
    /**
     * Handles the given packet using the specified network handler and handling function.
     *
     * @param handler the network handler
     * @param packet the packet to handle
     */
    public void handle(H handler, P packet) {
        handleFunc.invoke(handler, packet);
    }
    
    /**
     * Retrieves the class of the packet associated with this descriptor.
     *
     * @return the packet class
     */
    public Class<? extends IPacketBase> getPacketClass() {
        return packetClass;
    }
}
