package fluff.network.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fluff.bin.stream.BinaryInputStream;
import fluff.bin.stream.BinaryOutputStream;

public interface IPacketChannel {
	
	BinaryInputStream prepareInput(InputStream socketIn) throws IOException;
	
	void finalizeInput(InputStream socketIn, BinaryInputStream in) throws IOException;
	
	BinaryOutputStream prepareOutput(OutputStream socketOut) throws IOException;
	
	void finalizeOutput(OutputStream socketOut, BinaryOutputStream out) throws IOException;
}
