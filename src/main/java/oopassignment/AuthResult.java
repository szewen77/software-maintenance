package oopassignment;

import oopassignment.domain.auth.EmployeeRecord;

public class AuthResult {
    private final boolean success;
    private final boolean locked;
    private final String message;
    private final EmployeeRecord user;

    public AuthResult(boolean success, boolean locked, String message, EmployeeRecord user) {
        this.success = success;
        this.locked = locked;
        this.message = message;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getMessage() {
        return message;
    }

    public EmployeeRecord getUser() {
        return user;
    }
}
