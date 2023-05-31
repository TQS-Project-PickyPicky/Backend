package tqs.project.backend.data.parcel;

public enum ParcelStatus {

    PLACED("Placed"), // Store created order to deliver the parcel
    IN_TRANSIT("In Transit"), // Courier collected the parcel from the store
    DELIVERED("Delivered"), // Courier delivered the parcel to the collection point
    COLLECTED("Collected"), // Client collected the parcel from the collection point
    RETURNED("Returned"); // Client returned the parcel to the collection point

    private final String stringValue;

    ParcelStatus(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
