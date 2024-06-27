package fluff.network.packet.channels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fluff.bin.Binary;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;

public class DefaultPacketChannel implements IPacketChannel {
	
	@Override
	public ByteArrayInputStream read(InputStream input) throws IOException, NetworkException {
		return new ByteArrayInputStream(Binary.LenBytes(input::read));
	}
	
	@Override
	public void write(OutputStream output, ByteArrayOutputStream bytes) throws IOException, NetworkException {
		Binary.LenBytes(output::write, bytes.toByteArray());
		output.flush();
	}
}
