package cms.model.entities;

// This is just a container class, you would never create an instance of it.
public final class Enums {

    // Private constructor to prevent instantiation
    private Enums() {
    }

    public static enum Status {
        Active, Inactive, Suspended, Blocked
    }

    public static enum Role {
        ADMIN, DOCTOR, RECEPTIONIST
    }

    public static enum AppointmentStatus {
        Scheduled, Completed, Cancelled
    }

    public static enum BillingStatus {
        Paid, Unpaid
    }

}