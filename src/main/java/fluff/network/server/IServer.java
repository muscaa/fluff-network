package fluff.network.server;

import fluff.functions.gen.Func;
import fluff.network.INetHandler;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;
import fluff.network.packet.IPacketOutbound;
import fluff.network.packet.PacketContext;

/**
 * Represents a server that handles network communication, including sending and receiving packets.
 */
public interface IServer {
    
    /**
     * Sends a packet to all connected clients.
     * 
     * @param packet the packet to send
     */
    void sendAll(IPacketOutbound packet);
    
    /**
     * Disconnects all connected clients.
     */
    void disconnectAll();
    
    /**
     * Starts the server.
     * 
     * @param async if true, the server will start asynchronously
     * @throws NetworkException if an error occurs while starting the server
     */
    void start(boolean async) throws NetworkException;
    
    /**
     * Stops the server.
     */
    void stop();
    
    /**
     * Checks if the server is currently running.
     * 
     * @return true if the server is running, false otherwise
     */
    boolean isRunning();
    
    /**
     * Sets the default packet context and handler for new connections.
     * 
     * @param <V> the type of the network handler
     * @param defaultContext the default packet context
     * @param defaultHandlerFunc a function that creates the default network handler
     */
    <V extends INetHandler> void setDefaultContext(PacketContext<? super V> defaultContext, Func<V> defaultHandlerFunc);
    
    /**
     * Sets the default packet channel for new connections.
     * 
     * @param defaultChannelFunc a function that creates the default packet channel
     */
    void setDefaultChannel(Func<? extends IPacketChannel> defaultChannelFunc);
}
