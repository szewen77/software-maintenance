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
import java.util.stream.Collectors;

public class JdbcTransactionRepository implements TransactionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcTransactionRepository.class);

    @Override
    public void saveTransaction(TransactionHeader header, List<TransactionItem> items) {
        String headerSql = """
                INSERT INTO transaction_header(transaction_id, datetime, member_id, customer_id, total_amount, payment_method)
                VALUES(?,?,?,?,?,?)
                ON CONFLICT(transaction_id) DO UPDATE SET
                    datetime=excluded.datetime,
                    member_id=excluded.member_id,
                    customer_id=excluded.customer_id,
                    total_amount=excluded.total_amount,
                    payment_method=excluded.payment_method
                """;
        String itemSql = """
                INSERT INTO transaction_item(transaction_id, line_no, product_id, size, quantity, unit_price)
                VALUES(?,?,?,?,?,?)
                ON CONFLICT(transaction_id, line_no) DO UPDATE SET
                    product_id=excluded.product_id,
                    size=excluded.size,
                    quantity=excluded.quantity,
                    unit_price=excluded.unit_price
                """;
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(headerSql)) {
                    ps.setString(1, header.getTransactionId());
                    ps.setString(2, header.getDateTime().toString());
                    ps.setString(3, header.getMemberId());
                    ps.setString(4, header.getCustomerId());
                    ps.setDouble(5, header.getTotalAmount());
                    ps.setString(6, header.getPaymentMethod());
                    ps.executeUpdate();
                }
                for (TransactionItem item : items) {
                    try (PreparedStatement psItem = conn.prepareStatement(itemSql)) {
                        psItem.setString(1, item.getTransactionId());
                        psItem.setInt(2, item.getLineNo());
                        psItem.setString(3, item.getProductId());
                        psItem.setString(4, item.getSize());
                        psItem.setInt(5, item.getQuantity());
                        psItem.setDouble(6, item.getUnitPrice());
                        psItem.executeUpdate();
                    }
                }
                conn.commit();
                LOG.info("Transaction {} persisted with {} line items", header.getTransactionId(), items.size());
            } catch (SQLException e) {
                conn.rollback();
                LOG.error("Failed to save transaction {}", header.getTransactionId(), e);
            }
        } catch (SQLException e) {
            LOG.error("DB error while saving transaction {}", header.getTransactionId(), e);
        }
    }

    @Override
    public List<TransactionHeader> findAllHeaders() {
        List<TransactionHeader> headers = new ArrayList<>();
        String sql = "SELECT * FROM transaction_header ORDER BY datetime";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                headers.add(mapHeader(rs));
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch transaction headers", e);
        }
        return headers;
    }

    @Override
    public List<TransactionItem> findItemsByTransaction(String transactionId) {
        List<TransactionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM transaction_item WHERE transaction_id = ? ORDER BY line_no";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItem(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch items for transaction {}", transactionId, e);
        }
        return items.stream().collect(Collectors.toList());
    }

    private TransactionHeader mapHeader(ResultSet rs) throws SQLException {
        return new TransactionHeader(
                rs.getString("transaction_id"),
                java.time.LocalDateTime.parse(rs.getString("datetime")),
                rs.getString("member_id"),
                rs.getString("customer_id"),
                rs.getDouble("total_amount"),
                rs.getString("payment_method")
        );
    }

    private TransactionItem mapItem(ResultSet rs) throws SQLException {
        return new TransactionItem(
                rs.getString("transaction_id"),
                rs.getInt("line_no"),
                rs.getString("product_id"),
                rs.getString("size"),
                rs.getInt("quantity"),
                rs.getDouble("unit_price")
        );
    }
}
