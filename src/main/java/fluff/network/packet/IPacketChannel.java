package fluff.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fluff.network.NetworkException;

public interface IPacketChannel {
	
	ByteArrayInputStream read(InputStream socketIn) throws IOException, NetworkException;
	
	void write(OutputStream socketOut, ByteArrayOutputStream bytes) throws IOException, NetworkException;
}
