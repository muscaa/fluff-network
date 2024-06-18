package fluff.network;

public class NetworkException extends Exception {
	
	private static final long serialVersionUID = 2655478967302280355L;
	
	public NetworkException() {
        super();
    }
	
    public NetworkException(String message) {
        super(message);
    }
    
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NetworkException(Throwable cause) {
        super(cause);
    }
}
