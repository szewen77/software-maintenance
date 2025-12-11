package oopassignment.repository.impl;
import oopassignment.*;
import oopassignment.domain.auth.*;
import oopassignment.domain.member.*;
import oopassignment.domain.product.*;
import oopassignment.domain.order.*;
import oopassignment.repository.*;
import oopassignment.util.*;
import oopassignment.config.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<String, MemberRecord> members = new LinkedHashMap<>();

    public InMemoryMemberRepository() {
        seedDefaults();
    }

    private void seedDefaults() {
        MemberRecord member = new MemberRecord(
                "MB001",
                "Alex Member",
                "990101010101",
                100.0,
                LocalDate.now().minusDays(10),
                MemberStatus.ACTIVE);
        save(member);
    }

    @Override
    public Optional<MemberRecord> findById(String memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    @Override
    public Optional<MemberRecord> findByIc(String icNumber) {
        for (MemberRecord record : members.values()) {
            if (record.getIcNumber().equals(icNumber)) {
                return Optional.of(record);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<MemberRecord> searchByName(String keyword) {
        List<MemberRecord> results = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            return results;
        }
        String lower = keyword.toLowerCase();
        for (MemberRecord record : members.values()) {
            if (record.getName().toLowerCase().contains(lower)) {
                results.add(record);
            }
        }
        return results;
    }

    @Override
    public void save(MemberRecord member) {
        members.put(member.getMemberId(), member);
    }

    @Override
    public void update(MemberRecord member) {
        members.put(member.getMemberId(), member);
    }

    @Override
    public List<MemberRecord> findAll() {
        return new ArrayList<>(members.values());
    }
}
