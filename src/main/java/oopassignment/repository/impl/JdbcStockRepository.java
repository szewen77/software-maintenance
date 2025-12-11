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

public class JdbcStockRepository implements StockRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcStockRepository.class);

    @Override
    public List<StockItem> findByProductId(String productId) {
        // Use UPPER() for case-insensitive comparison
        String sql = "SELECT * FROM stock WHERE UPPER(product_id) = UPPER(?)";
        List<StockItem> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch stock rows for {}", productId, e);
        }
        return list;
    }

    @Override
    public int getQuantity(String productId, String size) {
        // Use UPPER() for case-insensitive comparison
        String sql = "SELECT quantity FROM stock WHERE UPPER(product_id) = UPPER(?) AND UPPER(size) = UPPER(?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            ps.setString(2, size);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to get quantity for {} size {}", productId, size, e);
        }
        return 0;
    }

    @Override
    public int getTotalQuantity(String productId) {
        String sql = "SELECT SUM(quantity) AS total FROM stock WHERE product_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to get total quantity for {}", productId, e);
        }
        return 0;
    }

    @Override
    public void setQuantity(String productId, String size, int qty) {
        // Normalize to uppercase before storing
        String normalizedProductId = productId != null ? productId.toUpperCase().trim() : null;
        String normalizedSize = size != null ? size.toUpperCase().trim() : null;
        String sql = """
                INSERT INTO stock(product_id,size,quantity)
                VALUES(?,?,?)
                ON CONFLICT(product_id,size) DO UPDATE SET quantity=excluded.quantity
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizedProductId);
            ps.setString(2, normalizedSize);
            ps.setInt(3, qty);
            ps.executeUpdate();
            LOG.info("Set stock for {} size {} = {}", normalizedProductId, normalizedSize, qty);
        } catch (SQLException e) {
            LOG.error("Failed to set quantity for {} size {}", normalizedProductId, normalizedSize, e);
        }
    }

    @Override
    public void increaseQuantity(String productId, String size, int qty) {
        int current = getQuantity(productId, size);
        setQuantity(productId, size, current + qty);
    }

    @Override
    public void decreaseQuantity(String productId, String size, int qty) {
        int current = getQuantity(productId, size);
        setQuantity(productId, size, current - qty);
    }

    private StockItem map(ResultSet rs) throws SQLException {
        return new StockItem(
                rs.getString("product_id"),
                rs.getString("size"),
                rs.getInt("quantity")
        );
    }
}
