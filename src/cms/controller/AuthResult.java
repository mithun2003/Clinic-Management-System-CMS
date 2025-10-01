package cms.controller;

import cms.model.entities.User;

/**
 * A class to hold the result of an authentication attempt. It contains the User
 * object on success, or an error message on failure.
 */
public class AuthResult {

    public enum AuthStatus {
        SUCCESS,
        INVALID_CREDENTIALS,
        CLINIC_SUSPENDED,
        USER_BLOCKED,
        CLINIC_NOT_FOUND
    }

    private final User user;
    private final AuthStatus status;
    private final String clinicCode;

    // Constructor for a successful login
    public AuthResult(User user) {
        this.user = user;
        this.status = AuthStatus.SUCCESS;
        this.clinicCode = null;
    }

    // Constructor for a failed login
    public AuthResult(AuthStatus status) {
        this.user = null;
        this.status = status;
        this.clinicCode = null; // No clinic code needed on failure
    }

    // NEW CONSTRUCTOR FOR CLINIC_NOT_FOUND
    public AuthResult(AuthStatus status, String clinicCode) {
        this.user = null;
        this.status = status;
        this.clinicCode = clinicCode; // Store the clinic code
    }

    public boolean isSuccess() {
        return status == AuthStatus.SUCCESS;
    }

    public User getUser() {
        return user;
    }

    public AuthStatus getStatus() {
        return status;
    }

    // A helper to get a user-friendly error message
    public String getErrorMessage() {
        return switch (status) {
            case INVALID_CREDENTIALS -> "Invalid username or password. Please try again.";
            case CLINIC_SUSPENDED -> "This clinic has been suspended. Please contact support.";
            case USER_BLOCKED -> "Your account has been blocked. Please contact your clinic administrator.";
            case CLINIC_NOT_FOUND -> "The clinic with code '" + clinicCode + "' does not exist.";
            case SUCCESS -> "Login successful.";
            default -> "An unknown authentication error occurred.";
        };
    }
}
