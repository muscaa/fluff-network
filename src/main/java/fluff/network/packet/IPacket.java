package fluff.network.packet;

/**
 * Represents a packet that can be both inbound and outbound in the network communication system.
 * This interface extends {@link IPacketInbound} and {@link IPacketOutbound},
 * indicating that implementing classes can be both read from and written to a binary stream.
 */
public interface IPacket extends IPacketInbound, IPacketOutbound {
	
}
