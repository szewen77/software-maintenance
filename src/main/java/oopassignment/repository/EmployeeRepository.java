package oopassignment.repository;

import java.util.List;
import java.util.Optional;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.EmploymentStatus;
import oopassignment.domain.auth.Role;

public interface EmployeeRepository {
    Optional<EmployeeRecord> findByUsername(String username);

    Optional<EmployeeRecord> findById(String id);

    List<EmployeeRecord> findByRole(Role role);

    List<EmployeeRecord> findAll();

    void save(EmployeeRecord employee);

    void update(EmployeeRecord employee);

    long countByRoleAndStatus(Role role, EmploymentStatus status);
}
