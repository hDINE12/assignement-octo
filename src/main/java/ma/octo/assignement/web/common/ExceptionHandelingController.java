package ma.octo.assignement.web.common;

import ma.octo.assignement.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionHandelingController {

    @ExceptionHandler(SoldeDisponibleInsuffisantException.class)
    public ResponseEntity<String> handleSoldeDisponibleInsuffisantException(Exception ex, WebRequest request) {
        return new ResponseEntity<>("Pas de solde pas de virement", null, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
    }

    @ExceptionHandler(CompteNonExistantException.class)
    public ResponseEntity<String> handleCompteNonExistantException(Exception ex, WebRequest request) {
        return new ResponseEntity<>("Compte introuvable", null, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RIBNonExistantException.class)
    public ResponseEntity<String> handleRIBNonExistantException(Exception ex, WebRequest request) {
        return new ResponseEntity<>("RIB introuvable", null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UtilisateurNonExistantException.class)
    public ResponseEntity<String> handleUtilisateurNonExistantException(Exception ex, WebRequest request) {
        return new ResponseEntity<>("RIB introuvable", null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnexpectedErrorException.class)
    public ResponseEntity<String> handleUnexpectedErrorException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), null, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
    }
}
