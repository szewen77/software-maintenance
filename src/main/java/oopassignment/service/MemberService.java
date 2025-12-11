package oopassignment.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import oopassignment.domain.member.MemberRecord;
import oopassignment.domain.member.MemberStatus;
import oopassignment.exception.DuplicateEntityException;
import oopassignment.exception.EntityNotFoundException;
import oopassignment.exception.InvalidInputException;
import oopassignment.repository.MemberRepository;
import oopassignment.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberService {
    private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);
    private static final Pattern IC_PATTERN = Pattern.compile("\\d{6}-?\\d{2}-?\\d{4}");

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public MemberRecord registerMember(String name, String icNumber, double initialCredit) {
        String cleanName = ValidationUtils.requireNotBlank(name, "Name");
        String normalizedIc = normalizeIc(icNumber);
        if (!isValidIc(normalizedIc)) {
            throw new InvalidInputException("IC format invalid");
        }
        if (repository.findByIc(normalizedIc).isPresent()) {
            throw new DuplicateEntityException("Duplicate IC number");
        }
        ValidationUtils.requireNonNegative(initialCredit, "Initial credit");

        String memberId = generateId();
        MemberRecord member = new MemberRecord(
                memberId,
                cleanName,
                normalizedIc,
                initialCredit,
                LocalDate.now(),
                MemberStatus.ACTIVE
        );
        repository.save(member);
        LOG.info("Registered member {} ({})", memberId, normalizedIc);
        return member;
    }

    public MemberRecord topUpCredit(String memberId, double amount) {
        ValidationUtils.requirePositive(amount, "Top up amount");
        MemberRecord member = getMemberOrThrow(memberId);
        member.setCreditBalance(member.getCreditBalance() + amount);
        repository.update(member);
        LOG.info("Topped up member {} by {}", memberId, amount);
        return member;
    }

    public Optional<MemberRecord> findById(String memberId) {
        String normalized = normalizeId(memberId);
        if (normalized.isEmpty()) {
            return Optional.empty();
        }
        return repository.findById(normalized);
    }

    public Optional<MemberRecord> findByIc(String icNumber) {
        return repository.findByIc(normalizeIc(icNumber));
    }

    public List<MemberRecord> searchByName(String keyword) {
        return repository.searchByName(keyword);
    }

    public List<MemberRecord> findAll() {
        return repository.findAll();
    }

    public boolean isValidIc(String icNumber) {
        if (icNumber == null) {
            return false;
        }
        return IC_PATTERN.matcher(icNumber).matches();
    }

    public String maskIc(String icNumber) {
        String normalized = normalizeIc(icNumber);
        if (normalized.length() < 8) {
            return "****";
        }
        String firstPart = normalized.substring(0, 6);
        String middlePart = normalized.substring(6, 8);
        return firstPart + "-" + middlePart + "-****";
    }

    private String normalizeIc(String icNumber) {
        return icNumber == null ? "" : icNumber.replaceAll("[^0-9]", "");
    }

    private MemberRecord getMemberOrThrow(String memberId) {
        String normalized = normalizeId(memberId);
        return repository.findById(normalized)
                .orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
    }

    private String generateId() {
        int max = 0;
        for (MemberRecord member : repository.findAll()) {
            String memberId = member.getMemberId();
            if (memberId != null && memberId.startsWith("MB")) {
                try {
                    int num = Integer.parseInt(memberId.substring(2));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignore) {
                    // skip malformed ids
                }
            }
        }
        return "MB" + String.format("%03d", max + 1);
    }

    private String normalizeId(String id) {
        if (id == null) {
            return "";
        }
        String trimmed = id.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.toUpperCase();
    }
}
