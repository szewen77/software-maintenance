package oopassignment.repository.impl;
import oopassignment.*;
import oopassignment.domain.auth.*;
import oopassignment.domain.member.*;
import oopassignment.domain.product.*;
import oopassignment.domain.order.*;
import oopassignment.repository.*;
import oopassignment.util.*;
import oopassignment.config.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<String, EmployeeRecord> employeesByUsername = new LinkedHashMap<>();
    private final Map<String, EmployeeRecord> employeesById = new LinkedHashMap<>();

    public InMemoryEmployeeRepository(PasswordHasher hasher) {
        seedDefaults(hasher);
    }

    private void seedDefaults(PasswordHasher hasher) {
        EmployeeRecord manager = new EmployeeRecord(
                "M001",
                "manager",
                hasher.hash("password123"),
                Role.MANAGER,
                5000.0,
                0.20,
                null,
                EmploymentStatus.ACTIVE);

        EmployeeRecord staff = new EmployeeRecord(
                "S001",
                "staff",
                hasher.hash("password123"),
                Role.STAFF,
                3000.0,
                0.10,
                manager.getId(),
                EmploymentStatus.ACTIVE);

        save(manager);
        save(staff);
    }

    @Override
    public Optional<EmployeeRecord> findByUsername(String username) {
        return Optional.ofNullable(employeesByUsername.get(username));
    }

    @Override
    public Optional<EmployeeRecord> findById(String id) {
        return Optional.ofNullable(employeesById.get(id));
    }

    @Override
    public List<EmployeeRecord> findByRole(Role role) {
        List<EmployeeRecord> matches = new ArrayList<>();
        for (EmployeeRecord employee : employeesById.values()) {
            if (employee.getRole() == role) {
                matches.add(employee);
            }
        }
        return matches;
    }

    @Override
    public List<EmployeeRecord> findAll() {
        return new ArrayList<>(employeesById.values());
    }

    @Override
    public void save(EmployeeRecord employee) {
        employeesByUsername.put(employee.getUsername(), employee);
        employeesById.put(employee.getId(), employee);
    }

    @Override
    public void update(EmployeeRecord employee) {
        employeesByUsername.put(employee.getUsername(), employee);
        employeesById.put(employee.getId(), employee);
    }

    @Override
    public long countByRoleAndStatus(Role role, EmploymentStatus status) {
        return employeesById.values().stream()
                .filter(e -> e.getRole() == role && e.getStatus() == status)
                .count();
    }
}
