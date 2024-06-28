package fluff.network;

/**
 * Exception class for network-related errors.
 */
public class NetworkException extends Exception {
    
    private static final long serialVersionUID = 2655478967302280355L;
    
    /**
     * Constructs a new NetworkException with no detail message.
     */
    public NetworkException() {
        super();
    }
    
    /**
     * Constructs a new NetworkException with the specified detail message.
     * 
     * @param message the detail message
     */
    public NetworkException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new NetworkException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new NetworkException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public NetworkException(Throwable cause) {
        super(cause);
    }
}
