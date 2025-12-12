package oopassignment.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import oopassignment.config.AppConfig;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.Role;
import oopassignment.repository.EmployeeRepository;
import oopassignment.util.PasswordHasher;
import oopassignment.AuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_ATTEMPTS = AppConfig.MAX_LOGIN_ATTEMPTS;
    private static final long LOCK_DURATION_MS = AppConfig.LOCK_DURATION_MS;

    private final EmployeeRepository repository;
    private final PasswordHasher hasher;
    private final Map<String, Integer> attempts = new HashMap<>();
    private final Map<String, Long> lockUntil = new HashMap<>();

    public AuthService(EmployeeRepository repository, PasswordHasher hasher) {
        this.repository = repository;
        this.hasher = hasher;
    }

    public AuthResult login(String username, String password) {
        long now = System.currentTimeMillis();
        if (isLocked(username, now)) {
            long remaining = lockUntil.get(username) - now;
            String msg = "Account locked. Try again in " + Math.max(1, remaining / 1000) + "s.";
            LOG.warn("Login blocked for locked user {}", username);
            return new AuthResult(false, true, msg, null);
        }
        Optional<EmployeeRecord> employeeOpt = repository.findByUsername(username);
        if (employeeOpt.isEmpty()) {
            boolean locked = recordFailure(username, now);
            LOG.warn("Login failed for missing user {}", username);
            return new AuthResult(false, locked, "Invalid username or password.", null);
        }
        EmployeeRecord employee = employeeOpt.get();
        if (!employee.isActive()) {
            LOG.warn("Inactive account login attempt for {}", username);
            return new AuthResult(false, false, "Account is inactive.", null);
        }

        String hashedInput = hasher.hash(password);
        if (hashedInput.equals(employee.getPasswordHash())) {
            resetAttempts(username);
            LOG.info("User {} logged in successfully", username);
            return new AuthResult(true, false, "Login successful.", employee);
        } else {
            boolean locked = recordFailure(username, now);
            LOG.warn("Login failed for {}", username);
            return new AuthResult(false, locked, "Invalid username or password.", null);
        }
    }

    public boolean hasRole(EmployeeRecord user, Role role) {
        return user != null && user.getRole() == role;
    }

    private boolean recordFailure(String username, long now) {
        int current = attempts.getOrDefault(username, 0) + 1;
        attempts.put(username, current);
        if (current >= MAX_ATTEMPTS) {
            lockUntil.put(username, now + LOCK_DURATION_MS);
            attempts.put(username, 0);
            return true;
        }
        return false;
    }

    private boolean isLocked(String username, long now) {
        Long lockedUntil = lockUntil.get(username);
        if (lockedUntil == null) {
            return false;
        }

        if (now >= lockedUntil) {
            lockUntil.remove(username);
            attempts.put(username, 0);
            return false;
        }
        return true;
    }

    private void resetAttempts(String username) {
        attempts.put(username, 0);
        lockUntil.remove(username);
    }
}
