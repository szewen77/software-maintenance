package oopassignment;

import oopassignment.domain.member.MemberRecord;
import oopassignment.exception.DuplicateEntityException;
import oopassignment.exception.InvalidInputException;
import oopassignment.repository.MemberRepository;
import oopassignment.repository.impl.InMemoryMemberRepository;
import oopassignment.service.MemberService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class MemberServiceTest {

    private MemberService memberService;

    @Before
    public void setUp() {
        MemberRepository repository = new InMemoryMemberRepository();
        memberService = new MemberService(repository);
    }

    @Test
    public void registerMemberWithValidIc() {
        MemberRecord member = memberService.registerMember("Jamie", "990101-01-1111", 50.0);
        assertTrue("Member should be created for valid IC", member != null);
    }

    @Test
    public void rejectDuplicateIc() {
        memberService.registerMember("Jamie", "990101-01-1111", 50.0);
        assertThrows("Duplicate IC should be rejected",
                DuplicateEntityException.class,
                () -> memberService.registerMember("Duplicate", "990101-01-1111", 10.0));
    }

    @Test
    public void rejectInvalidIc() {
        assertThrows("Invalid IC format should be rejected",
                InvalidInputException.class,
                () -> memberService.registerMember("Invalid", "1234", 0));
    }

    @Test
    public void topUpIncreasesBalance() {
        MemberRecord member = memberService.registerMember("Top Up", "010101-01-2222", 0);
        memberService.topUpCredit(member.getMemberId(), 100.0);
        MemberRecord refreshed = memberService.findById(member.getMemberId()).orElseThrow();
        assertEquals("Top up should increase balance", 100.0, refreshed.getCreditBalance(), 0.0001);
    }

    @Test
    public void rejectNegativeTopUp() {
        MemberRecord member = memberService.registerMember("Reject Negative", "020202-02-3333", 0);
        assertThrows("Negative top up should be rejected",
                InvalidInputException.class,
                () -> memberService.topUpCredit(member.getMemberId(), -10.0));
    }
}
