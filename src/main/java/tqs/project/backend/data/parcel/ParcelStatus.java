package ies.project.backend.data.parcel;

public enum ParcelStatus {
    PLACED, // Store created order to deliver the parcel
    IN_TRANSIT, // Courier collected the parcel from the store
    DELIVERED, // Courier delivered the parcel to the collection point
    COLLECTED, // Client collected the parcel from the collection point
    RETURNED // Courier returned the parcel to the collection point
}
