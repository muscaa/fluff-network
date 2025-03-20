package fluff.network.packet.channels;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import fluff.bin.Binary;
import fluff.network.NetworkException;
import fluff.network.packet.IPacketChannel;

/**
 * The EncryptedPacketChannel class implements the IPacketChannel interface
 * and provides methods for reading and writing encrypted data using
 * input and output streams.
 */
public class EncryptedPacketChannel implements IPacketChannel {
    
    private final String cipherTransformation;
    private final int ivSize;
    private final SecretKey secretKey;
    
    /**
     * Constructs an EncryptedPacketChannel with the specified cipher transformation, IV size, and secret key.
     *
     * @param cipherTransformation the cipher transformation to use (e.g., "AES/CBC/PKCS5Padding")
     * @param ivSize the size of the initialization vector (IV)
     * @param secretKey the secret key to use for encryption and decryption
     */
    public EncryptedPacketChannel(String cipherTransformation, int ivSize, SecretKey secretKey) {
        this.cipherTransformation = cipherTransformation;
        this.ivSize = ivSize;
        this.secretKey = secretKey;
    }
    
    /**
     * Constructs an EncryptedPacketChannel with the specified cipher transformation and secret key.
     * Uses a default IV size of 16 bytes.
     *
     * @param cipherTransformation the cipher transformation to use (e.g., "AES/CBC/PKCS5Padding")
     * @param secretKey the secret key to use for encryption and decryption
     */
    public EncryptedPacketChannel(String cipherTransformation, SecretKey secretKey) {
        this(cipherTransformation, 16, secretKey);
    }
    
    /**
     * Constructs an EncryptedPacketChannel with the specified IV size and secret key.
     * Uses a default cipher transformation of "AES/CBC/PKCS5Padding".
     *
     * @param ivSize the size of the initialization vector (IV)
     * @param secretKey the secret key to use for encryption and decryption
     */
    public EncryptedPacketChannel(int ivSize, SecretKey secretKey) {
        this("AES/CBC/PKCS5Padding", ivSize, secretKey);
    }
    
    /**
     * Constructs an EncryptedPacketChannel with the specified secret key.
     * Uses a default cipher transformation of "AES/CBC/PKCS5Padding" and an IV size of 16 bytes.
     *
     * @param secretKey the secret key to use for encryption and decryption
     */
    public EncryptedPacketChannel(SecretKey secretKey) {
        this(16, secretKey);
    }
    
    @Override
    public ByteArrayInputStream read(BufferedInputStream input) throws IOException, NetworkException {
        try {
        	int len = Binary.Int(input::read);
        	if (len == -1) return EMPTY;
        	
            byte[] iv = Binary.Bytes(input::read, ivSize);
            byte[] encrypted = Binary.Bytes(input::read, len);
            
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(encrypted);
            
            return new ByteArrayInputStream(decrypted);
        } catch (GeneralSecurityException e) {
            throw new NetworkException(e);
        }
    }
    
    @Override
    public void write(BufferedOutputStream output, ByteArrayOutputStream bytes) throws IOException, NetworkException {
        try {
            byte[] iv = new byte[ivSize];
            new SecureRandom().nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(bytes.toByteArray());
            
            Binary.Int(output::write, encrypted.length);
            Binary.Bytes(output::write, iv, ivSize);
            Binary.Bytes(output::write, encrypted, encrypted.length);
            
            output.flush();
        } catch (GeneralSecurityException e) {
            throw new NetworkException(e);
        }
    }
}
