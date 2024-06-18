package fluff.network.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IPacketChannel<I extends InputStream, O extends OutputStream> {
	
	I openInput(InputStream socketIn) throws IOException;
	
	void closeInput(InputStream socketIn, I in) throws IOException;
	
	O openOutput(OutputStream socketOut) throws IOException;
	
	void closeOutput(OutputStream socketOut, O out) throws IOException;
}
