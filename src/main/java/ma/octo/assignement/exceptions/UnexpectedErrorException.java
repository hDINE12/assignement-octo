package ma.octo.assignement.exceptions;

public class UnexpectedErrorException extends Exception{
    public UnexpectedErrorException() {
    }

    public UnexpectedErrorException(String message) {
        super(message);
    }
}
