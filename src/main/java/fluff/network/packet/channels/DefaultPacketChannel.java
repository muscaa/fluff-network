package fluff.network.packet.channels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fluff.network.packet.IPacketChannel;

public class DefaultPacketChannel implements IPacketChannel<InputStream, OutputStream> {
	
	public static final IPacketChannel INSTANCE = new DefaultPacketChannel();
	
	@Override
	public InputStream openInput(InputStream socketIn) throws IOException {
		return socketIn;
	}
	
	@Override
	public void closeInput(InputStream socketIn, InputStream in) throws IOException {}
	
	@Override
	public OutputStream openOutput(OutputStream socketOut) throws IOException {
		return socketOut;
	}
	
	@Override
	public void closeOutput(OutputStream socketOut, OutputStream out) throws IOException {
		socketOut.flush();
	}
}
