package fluff.network.packet.channels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import fluff.bin.Binary;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;

public class EncryptedPacketChannel implements IPacketChannel {
	
	private final String cipherTransformation;
	private final int ivSize;
	private final SecretKey secretKey;
	
	public EncryptedPacketChannel(String cipherTransformation, int ivSize, SecretKey secretKey) {
		this.cipherTransformation = cipherTransformation;
		this.ivSize = ivSize;
		this.secretKey = secretKey;
	}
	
	public EncryptedPacketChannel(String cipherTransformation, SecretKey secretKey) {
		this(cipherTransformation, 16, secretKey);
	}
	
	public EncryptedPacketChannel(int ivSize, SecretKey secretKey) {
		this("AES/CBC/PKCS5Padding", ivSize, secretKey);
	}
	
	public EncryptedPacketChannel(SecretKey secretKey) {
		this("AES/CBC/PKCS5Padding", 16, secretKey);
	}
	
	@Override
	public ByteArrayInputStream read(InputStream input) throws IOException, NetworkException {
        try {
    		byte[] iv = Binary.Bytes(input::read, ivSize);
    		byte[] encrypted = Binary.LenBytes(input::read);
    		
    		Cipher cipher = Cipher.getInstance(cipherTransformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(encrypted);
            
            return new ByteArrayInputStream(decrypted);
		} catch (GeneralSecurityException e) {
			throw new NetworkException(e);
		}
	}
	
	@Override
	public void write(OutputStream output, ByteArrayOutputStream bytes) throws IOException, NetworkException {
        try {
    		byte[] iv = new byte[ivSize];
    		new SecureRandom().nextBytes(iv);
        	
        	Cipher cipher = Cipher.getInstance(cipherTransformation);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
			byte[] encrypted = cipher.doFinal(bytes.toByteArray());
			
			Binary.Bytes(output::write, iv, ivSize);
			Binary.LenBytes(output::write, encrypted);
			
			output.flush();
		} catch (GeneralSecurityException e) {
			throw new NetworkException(e);
		}
	}
}
