package fluff.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fluff.network.NetworkException;

public interface IPacketChannel {
	
	ByteArrayInputStream read(InputStream input) throws IOException, NetworkException;
	
	void write(OutputStream output, ByteArrayOutputStream bytes) throws IOException, NetworkException;
}
