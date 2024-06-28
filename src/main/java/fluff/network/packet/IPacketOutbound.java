package fluff.network.packet;

import fluff.bin.data.IBinaryWritable;

/**
 * Represents an outbound packet in the network communication system.
 * This interface extends {@link IPacketBase} and {@link IBinaryWritable},
 * indicating that implementing classes can be written to a binary stream.
 */
public interface IPacketOutbound extends IPacketBase, IBinaryWritable {
	
}
