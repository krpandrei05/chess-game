package exceptions;

public class GameFlowControlException extends RuntimeException{
    public GameFlowControlException(String message) {
        super(message);
    }
    public GameFlowControlException(String message, Throwable cause) {
        super(message, cause);
    }
}
