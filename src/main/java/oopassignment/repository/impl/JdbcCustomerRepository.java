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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCustomerRepository implements CustomerRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    @Override
    public Optional<CustomerRecord> findById(String customerId) {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch customer {}", customerId, e);
        }
        return Optional.empty();
    }

    @Override
    public void save(CustomerRecord customer) {
        upsert(customer);
    }

    @Override
    public void update(CustomerRecord customer) {
        upsert(customer);
    }

    @Override
    public List<CustomerRecord> findAll() {
        List<CustomerRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM customer ORDER BY customer_id";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch customers", e);
        }
        return list;
    }

    private void upsert(CustomerRecord customer) {
        String sql = """
                INSERT INTO customer(customer_id, name, registered_date, last_purchase_date)
                VALUES (?,?,?,?)
                ON CONFLICT(customer_id) DO UPDATE SET
                    name=excluded.name,
                    registered_date=excluded.registered_date,
                    last_purchase_date=excluded.last_purchase_date
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getCustomerId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getRegisteredDate().toString());
            ps.setString(4, customer.getLastPurchaseDate() == null ? null : customer.getLastPurchaseDate().toString());
            ps.executeUpdate();
            LOG.info("Upserted customer {}", customer.getCustomerId());
        } catch (SQLException e) {
            LOG.error("Failed to upsert customer {}", customer.getCustomerId(), e);
        }
    }

    private CustomerRecord map(ResultSet rs) throws SQLException {
        String lastPurchase = rs.getString("last_purchase_date");
        return new CustomerRecord(
                rs.getString("customer_id"),
                rs.getString("name"),
                LocalDate.parse(rs.getString("registered_date")),
                lastPurchase == null ? null : LocalDate.parse(lastPurchase)
        );
    }
}
