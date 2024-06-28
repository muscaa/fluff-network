package fluff.network.packet;

import fluff.bin.data.IBinaryReadable;

/**
 * Represents an inbound packet in the network communication system.
 * This interface extends {@link IPacketBase} and {@link IBinaryReadable},
 * indicating that implementing classes can be read from a binary stream.
 */
public interface IPacketInbound extends IPacketBase, IBinaryReadable {
	
}
