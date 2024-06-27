package fluff.network.packet.channels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fluff.bin.stream.BinaryInputStream;
import fluff.bin.stream.BinaryOutputStream;
import fluff.network.client.IClient;
import fluff.network.packet.IPacketChannel;

public class DefaultPacketChannel implements IPacketChannel {
	
	protected final IClient client;
	
	public DefaultPacketChannel(IClient client) {
		this.client = client;
	}
	
	@Override
	public BinaryInputStream prepareInput(InputStream socketIn) throws IOException {
		return new BinaryInputStream(socketIn);
	}
	
	@Override
	public void finalizeInput(InputStream socketIn, BinaryInputStream in) throws IOException {}
	
	@Override
	public BinaryOutputStream prepareOutput(OutputStream socketOut) throws IOException {
		return new BinaryOutputStream(socketOut);
	}
	
	@Override
	public void finalizeOutput(OutputStream socketOut, BinaryOutputStream out) throws IOException {
		out.flush();
	}
}
