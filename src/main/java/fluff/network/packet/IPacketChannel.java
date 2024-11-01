package fluff.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fluff.network.NetworkException;

/**
 * Represents a channel for reading and writing packets in the network communication system.
 * This interface defines methods for reading from an input stream and writing to an output stream.
 */
public interface IPacketChannel {
	
	/**
	 * Empty byte array.
	 */
	ByteArrayInputStream EMPTY = new ByteArrayInputStream(new byte[0]);
	
    /**
     * Reads data from the given input stream and returns it as a {@link ByteArrayInputStream}.
     *
     * @param input the input stream to read from
     * @return a {@link ByteArrayInputStream} containing the read data
     * @throws IOException if an I/O error occurs while reading from the stream
     * @throws NetworkException if a network-related error occurs while reading from the stream
     */
    ByteArrayInputStream read(InputStream input) throws IOException, NetworkException;
    
    /**
     * Writes data to the given output stream from the provided {@link ByteArrayOutputStream}.
     *
     * @param output the output stream to write to
     * @param bytes the {@link ByteArrayOutputStream} containing the data to write
     * @throws IOException if an I/O error occurs while writing to the stream
     * @throws NetworkException if a network-related error occurs while writing to the stream
     */
    void write(OutputStream output, ByteArrayOutputStream bytes) throws IOException, NetworkException;
}
