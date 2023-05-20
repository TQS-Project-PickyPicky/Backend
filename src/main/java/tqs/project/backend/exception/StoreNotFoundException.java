package tqs.project.backend.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(Integer id) {
        super(String.format("Store with id %d not found", id));
    }
}
