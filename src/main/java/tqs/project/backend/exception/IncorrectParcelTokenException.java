package tqs.project.backend.exception;

public class IncorrectParcelTokenException extends RuntimeException {
    public IncorrectParcelTokenException(Integer token, Integer parcelId) {
        super(String.format("Incorrect token %d for parcel with id %d", token, parcelId));
    }
}
