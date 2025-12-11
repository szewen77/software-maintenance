package oopassignment.repository;

import java.util.List;
import java.util.Optional;
import oopassignment.domain.member.MemberRecord;

public interface MemberRepository {
    Optional<MemberRecord> findById(String memberId);

    Optional<MemberRecord> findByIc(String icNumber);

    List<MemberRecord> searchByName(String keyword);

    void save(MemberRecord member);

    void update(MemberRecord member);

    List<MemberRecord> findAll();
}
