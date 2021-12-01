package ma.octo.assignement.exceptions;

public class UtilisateurNonExistantException extends Exception{
    public UtilisateurNonExistantException() {
    }

    public UtilisateurNonExistantException(String message) {
        super(message);
    }
}
