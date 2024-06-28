package fluff.network.simple;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import fluff.network.INetHandler;
import fluff.network.NetworkException;
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
     * @param socket the socket for the connection
     * @param context the packet context for the connection
     * @param handler the network handler for the connection
     * @param channel the packet channel for the connection
     * @throws IOException if an I/O error occurs when opening the connection
     * @throws NetworkException if a network-related error occurs when opening the connection
     */
    public SimpleClientConnection(AbstractServer server, UUID uuid, Socket socket, PacketContext<?> context, INetHandler handler, IPacketChannel channel) throws IOException, NetworkException {
        super(server);
        
        this.uuid = uuid;
        
        setContextUnsafe(context, handler);
        setChannel(channel);
        openConnection(socket);
    }
    
    @Override
    public UUID getUUID() {
        return uuid;
    }
}
