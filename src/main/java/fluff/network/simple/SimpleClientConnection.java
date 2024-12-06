package fluff.network.simple;

import java.util.UUID;

import fluff.network.INetHandler;
import fluff.network.packet.IPacketChannel;
import fluff.network.packet.PacketContext;
import fluff.network.server.AbstractClientConnection;
import fluff.network.server.AbstractServer;

/**
 * A simple implementation of the {@link AbstractClientConnection} class for managing client connections to a server.
 */
public class SimpleClientConnection extends AbstractClientConnection {
    
    private final UUID uuid;
    
    /**
     * Constructs a new SimpleClientConnection.
     * 
     * @param server the server to which the client is connecting
     * @param uuid the unique identifier of the client
     * @param context the packet context for the connection
     * @param handler the network handler for the connection
     * @param channel the packet channel for the connection
     */
    public SimpleClientConnection(AbstractServer server, UUID uuid, PacketContext<?> context, INetHandler handler, IPacketChannel channel) {
        super(server);
        
        this.uuid = uuid;
        
        setContextUnsafe(context, handler);
        setChannel(channel);
    }
    
    @Override
    public UUID getUUID() {
        return uuid;
    }
}
