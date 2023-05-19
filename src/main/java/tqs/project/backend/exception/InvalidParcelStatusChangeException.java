package tqs.project.backend.exception;

public class InvalidParcelStatusChangeException extends RuntimeException {

    public InvalidParcelStatusChangeException(String message) {
        super(message);
    }
}
