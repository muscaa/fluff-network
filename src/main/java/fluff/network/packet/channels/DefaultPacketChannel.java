package fluff.network.packet.channels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fluff.bin.Binary;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;

/**
 * The DefaultPacketChannel class implements the IPacketChannel interface
 * and provides default methods for reading and writing data using
 * input and output streams.
 */
public class DefaultPacketChannel implements IPacketChannel {
    
    @Override
    public ByteArrayInputStream read(InputStream input) throws IOException, NetworkException {
    	int len = Binary.Int(input::read);
    	if (len == -1) return EMPTY;
    	
        return new ByteArrayInputStream(Binary.Bytes(input::read, len));
    }
    
    @Override
    public void write(OutputStream output, ByteArrayOutputStream bytes) throws IOException, NetworkException {
        Binary.LenBytes(output::write, bytes.toByteArray());
        output.flush();
    }
}
