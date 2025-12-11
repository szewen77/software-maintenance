package oopassignment.repository.impl;
import oopassignment.*;
import oopassignment.domain.auth.*;
import oopassignment.domain.member.*;
import oopassignment.domain.product.*;
import oopassignment.domain.order.*;
import oopassignment.repository.*;
import oopassignment.util.*;
import oopassignment.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import oopassignment.config.Database;
import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.EmploymentStatus;
import oopassignment.domain.auth.Role;
import oopassignment.repository.EmployeeRepository;

public class JdbcEmployeeRepository implements EmployeeRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcEmployeeRepository.class);

    @Override
    public Optional<EmployeeRecord> findByUsername(String username) {
        String sql = "SELECT * FROM employee WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch employee by username {}", username, e);
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<EmployeeRecord> findById(String id) {
        String sql = "SELECT * FROM employee WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch employee {}", id, e);
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public List<EmployeeRecord> findByRole(Role role) {
        String sql = "SELECT * FROM employee WHERE role = ?";
        List<EmployeeRecord> records = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(map(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch employees by role {}", role, e);
        }
        return records;
    }

    @Override
    public List<EmployeeRecord> findAll() {
        String sql = "SELECT * FROM employee ORDER BY id";
        List<EmployeeRecord> records = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                records.add(map(rs));
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch employees", e);
        }
        return records;
    }

    @Override
    public void save(EmployeeRecord employee) {
        upsert(employee);
    }

    @Override
    public void update(EmployeeRecord employee) {
        upsert(employee);
    }

    @Override
    public long countByRoleAndStatus(Role role, EmploymentStatus status) {
        String sql = "SELECT COUNT(*) AS c FROM employee WHERE role = ? AND status = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            ps.setString(2, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("c");
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to count employees by role {} and status {}", role, status, e);
        }
        return 0;
    }

    private void upsert(EmployeeRecord e) {
        String sql = """
                INSERT INTO employee(id, username, password_hash, role, base_salary, bonus_rate, upline_id, status)
                VALUES(?,?,?,?,?,?,?,?)
                ON CONFLICT(id) DO UPDATE SET
                    username=excluded.username,
                    password_hash=excluded.password_hash,
                    role=excluded.role,
                    base_salary=excluded.base_salary,
                    bonus_rate=excluded.bonus_rate,
                    upline_id=excluded.upline_id,
                    status=excluded.status
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getId());
            ps.setString(2, e.getUsername());
            ps.setString(3, e.getPasswordHash());
            ps.setString(4, e.getRole().name());
            ps.setDouble(5, e.getBaseSalary());
            ps.setDouble(6, e.getBonusRate());
            ps.setString(7, e.getUplineId());
            ps.setString(8, e.getStatus().name());
            ps.executeUpdate();
            LOG.info("Upserted employee {}", e.getId());
        } catch (SQLException ex) {
            LOG.error("Failed to upsert employee {}", e.getId(), ex);
        }
    }

    private EmployeeRecord map(ResultSet rs) throws SQLException {
        return new EmployeeRecord(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role")),
                rs.getDouble("base_salary"),
                rs.getDouble("bonus_rate"),
                rs.getString("upline_id"),
                EmploymentStatus.valueOf(rs.getString("status"))
        );
    }
}
