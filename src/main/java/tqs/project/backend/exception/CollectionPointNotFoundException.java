package tqs.project.backend.exception;

public class CollectionPointNotFoundException extends RuntimeException {
    public CollectionPointNotFoundException(Integer id) {
        super(String.format("Collection Point with id %d not found", id));
    }
}
