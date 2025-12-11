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

public class JdbcProductRepository implements ProductRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcProductRepository.class);

    @Override
    public List<ProductRecord> findAll() {
        String sql = "SELECT * FROM product ORDER BY product_id";
        List<ProductRecord> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch products", e);
        }
        return list;
    }

    @Override
    public Optional<ProductRecord> findById(String id) {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch product {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public void save(ProductRecord product) {
        upsert(product);
    }

    @Override
    public void update(ProductRecord product) {
        upsert(product);
    }

    @Override
    public void delete(String productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            ps.executeUpdate();
            LOG.info("Deleted product {}", productId);
        } catch (SQLException e) {
            LOG.error("Failed to delete product {}", productId, e);
        }
    }

    private void upsert(ProductRecord product) {
        String sql = """
                INSERT INTO product(product_id,name,category,price,status)
                VALUES(?,?,?,?,?)
                ON CONFLICT(product_id) DO UPDATE SET
                    name=excluded.name,
                    category=excluded.category,
                    price=excluded.price,
                    status=excluded.status
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getCategory());
            ps.setDouble(4, product.getPrice());
            ps.setString(5, product.getStatus().name());
            ps.executeUpdate();
            LOG.info("Upserted product {}", product.getProductId());
        } catch (SQLException e) {
            LOG.error("Failed to upsert product {}", product.getProductId(), e);
        }
    }

    private ProductRecord map(ResultSet rs) throws SQLException {
        return new ProductRecord(
                rs.getString("product_id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("price"),
                ProductStatus.valueOf(rs.getString("status"))
        );
    }
}
