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

public class JdbcMemberRepository implements MemberRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcMemberRepository.class);

    @Override
    public Optional<MemberRecord> findById(String memberId) {
        String sql = "SELECT * FROM member WHERE member_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch member {}", memberId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<MemberRecord> findByIc(String icNumber) {
        String sql = "SELECT * FROM member WHERE ic_number = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, icNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch member by IC {}", icNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public List<MemberRecord> searchByName(String keyword) {
        List<MemberRecord> results = new ArrayList<>();
        String sql = "SELECT * FROM member WHERE lower(name) LIKE ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(map(rs));
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to search members by name {}", keyword, e);
        }
        return results;
    }

    @Override
    public void save(MemberRecord member) {
        upsert(member);
    }

    @Override
    public void update(MemberRecord member) {
        upsert(member);
    }

    @Override
    public List<MemberRecord> findAll() {
        List<MemberRecord> results = new ArrayList<>();
        String sql = "SELECT * FROM member ORDER BY member_id";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(map(rs));
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch members", e);
        }
        return results;
    }

    private void upsert(MemberRecord member) {
        String sql = """
                INSERT INTO member(member_id, name, ic_number, credit_balance, join_date, status)
                VALUES (?,?,?,?,?,?)
                ON CONFLICT(member_id) DO UPDATE SET
                    name=excluded.name,
                    ic_number=excluded.ic_number,
                    credit_balance=excluded.credit_balance,
                    join_date=excluded.join_date,
                    status=excluded.status
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getMemberId());
            ps.setString(2, member.getName());
            ps.setString(3, member.getIcNumber());
            ps.setDouble(4, member.getCreditBalance());
            ps.setString(5, member.getJoinDate().toString());
            ps.setString(6, member.getStatus().name());
            ps.executeUpdate();
            LOG.info("Upserted member {}", member.getMemberId());
        } catch (SQLException e) {
            LOG.error("Failed to upsert member {}", member.getMemberId(), e);
        }
    }

    private MemberRecord map(ResultSet rs) throws SQLException {
        return new MemberRecord(
                rs.getString("member_id"),
                rs.getString("name"),
                rs.getString("ic_number"),
                rs.getDouble("credit_balance"),
                LocalDate.parse(rs.getString("join_date")),
                MemberStatus.valueOf(rs.getString("status"))
        );
    }
}
