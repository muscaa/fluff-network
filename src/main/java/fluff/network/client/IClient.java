package fluff.network.client;

import fluff.network.INetHandler;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;
import fluff.network.packet.IPacketOutbound;
import fluff.network.packet.PacketContext;

/**
 * Interface representing a network client.
 */
public interface IClient {
    
    /**
     * Sends the specified outbound packet.
     * 
     * @param packet the outbound packet to send
     */
    void send(IPacketOutbound packet);
    
    /**
     * Disconnects the client.
     */
    void disconnect();
    
    /**
     * Called when the connection is established.
     * 
     * @throws NetworkException if a network error occurs
     */
    void onConnect() throws NetworkException;
    
    /**
     * Called when the connection is disconnected.
     */
    void onDisconnect();
    
    /**
     * Checks if the client is connected.
     * 
     * @return true if the client is connected, false otherwise
     */
    boolean isConnected();
    
    /**
     * Sets the packet context and handler for the client and initializes the handler.
     * 
     * @param <V> the type of the net handler
     * @param context the packet context
     * @param handler the net handler
     */
    <V extends INetHandler> void setContext(PacketContext<? super V> context, V handler);
    
    /**
     * Sets the packet channel for the client.
     * 
     * @param channel the packet channel
     */
    void setChannel(IPacketChannel channel);
}
