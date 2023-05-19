package tqs.project.backend.exception;

public class ParcelNotFoundException extends RuntimeException {
    public ParcelNotFoundException(Integer id) {
        super(String.format("Parcel with id %d not found", id));
    }
}
