package tqs.project.backend.exception;

import tqs.project.backend.data.parcel.ParcelStatus;

public class InvalidParcelStatusChangeException extends RuntimeException {
    public InvalidParcelStatusChangeException(ParcelStatus oldStatus, ParcelStatus newStatus) {
        super(String.format("Invalid status change from %s to %s", oldStatus.toString(), newStatus.toString()));
    }
}
