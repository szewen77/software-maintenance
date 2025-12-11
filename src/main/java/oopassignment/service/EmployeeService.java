package oopassignment.service;

import java.util.List;
import java.util.Optional;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.EmploymentStatus;
import oopassignment.domain.auth.Role;
import oopassignment.exception.DuplicateEntityException;
import oopassignment.exception.EntityNotFoundException;
import oopassignment.exception.InvalidInputException;
import oopassignment.exception.UnauthorizedActionException;
import oopassignment.repository.EmployeeRepository;
import oopassignment.util.PasswordHasher;
import oopassignment.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeService {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeService.class);
    private static final double MANAGER_BONUS = 0.20;
    private static final double STAFF_BONUS = 0.10;

    private final EmployeeRepository repository;
    private final PasswordHasher hasher;

    public EmployeeService(EmployeeRepository repository, PasswordHasher hasher) {
        this.repository = repository;
        this.hasher = hasher;
    }

    // Backward-compatible signature that ignores displayName (not stored in current model)
    public EmployeeRecord registerEmployee(String username, String displayName, String rawPassword, Role role,
                                           double baseSalary, String uplineId) {
        return registerEmployee(username, rawPassword, role, baseSalary, uplineId);
    }

    public EmployeeRecord registerEmployee(String username, String rawPassword, Role role,
                                           double baseSalary, String uplineId) {
        String cleanUsername = ValidationUtils.requireNotBlank(username, "Username");
        ValidationUtils.requireNotBlank(rawPassword, "Password");
        if (role == null) {
            throw new InvalidInputException("Role is required");
        }
        ValidationUtils.validateSalary(baseSalary);
        if (repository.findByUsername(cleanUsername).isPresent()) {
            throw new DuplicateEntityException("Username already exists");
        }

        String id = generateId(role);
        double bonusRate = role == Role.MANAGER ? MANAGER_BONUS : STAFF_BONUS;
        String passwordHash = hasher.hash(rawPassword);
        String managedUpline = ValidationUtils.validateRoleTransition(role, uplineId);

        EmployeeRecord employee = new EmployeeRecord(
                id,
                cleanUsername,
                passwordHash,
                role,
                baseSalary,
                bonusRate,
                managedUpline,
                EmploymentStatus.ACTIVE);

        repository.save(employee);
        LOG.info("Registered employee {} with role {}", id, role);
        return employee;
    }

    public EmployeeRecord modifyEmployee(String id, String displayName, Role role, double baseSalary, String uplineId) {
        EmployeeRecord existing = getEmployeeOrThrow(id);
        if (role != null) {
            existing.setRole(role);
        }
        if (baseSalary > 0) {
            ValidationUtils.validateSalary(baseSalary);
            existing.setBaseSalary(baseSalary);
        }
        String resolvedUpline = ValidationUtils.validateRoleTransition(existing.getRole(), uplineId);
        existing.setUplineId(resolvedUpline);
        existing.setBonusRate(existing.getRole() == Role.MANAGER ? MANAGER_BONUS : STAFF_BONUS);
        repository.update(existing);
        LOG.info("Modified employee {} role={}, salary={}", existing.getId(), existing.getRole(), existing.getBaseSalary());
        return existing;
    }

    public void updatePassword(String id, String newPassword) {
        ValidationUtils.requireNotBlank(newPassword, "Password");
        EmployeeRecord existing = getEmployeeOrThrow(id);
        existing.setPasswordHash(hasher.hash(newPassword));
        repository.update(existing);
        LOG.info("Password updated for {}", id);
    }

    public void deactivateEmployee(String targetId, String actingUserId) {
        if (targetId.equals(actingUserId)) {
            throw new UnauthorizedActionException("Cannot delete own account");
        }
        EmployeeRecord target = getEmployeeOrThrow(targetId);
        if (target.getRole() == Role.MANAGER
                && repository.countByRoleAndStatus(Role.MANAGER, EmploymentStatus.ACTIVE) <= 1) {
            throw new UnauthorizedActionException("Cannot delete the last manager");
        }
        target.setStatus(EmploymentStatus.INACTIVE);
        repository.update(target);
        LOG.warn("Deactivated employee {}", targetId);
    }

    public double calculateSalary(EmployeeRecord employee) {
        double bonusRate = employee.getBonusRate();
        if (bonusRate == 0) {
            bonusRate = employee.getRole() == Role.MANAGER ? MANAGER_BONUS : STAFF_BONUS;
        }
        return employee.getBaseSalary() * (1 + bonusRate);
    }

    public Optional<EmployeeRecord> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<EmployeeRecord> findById(String id) {
        String normalized = normalizeId(id);
        if (normalized.isEmpty()) {
            return Optional.empty();
        }
        return repository.findById(normalized);
    }

    private EmployeeRecord getEmployeeOrThrow(String id) {
        String normalized = normalizeId(id);
        return repository.findById(normalized)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    }

    private String generateId(Role role) {
        String prefix = role == Role.MANAGER ? "M" : "S";
        int max = 0;
        for (EmployeeRecord employee : repository.findAll()) {
            String empId = employee.getId();
            if (empId != null && empId.startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(empId.substring(1));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignore) {
                    // skip malformed ids
                }
            }
        }
        return prefix + String.format("%03d", max + 1);
    }

    public List<EmployeeRecord> listByRole(Role role) {
        return repository.findByRole(role);
    }

    private String normalizeId(String id) {
        if (id == null) {
            return "";
        }
        String trimmed = id.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.toUpperCase();
    }

}
