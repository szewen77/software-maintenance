package oopassignment;

import java.util.List;
import java.util.Optional;
import oopassignment.domain.member.MemberRecord;
import oopassignment.exception.DuplicateEntityException;
import oopassignment.exception.InvalidInputException;
import oopassignment.exception.EntityNotFoundException;
import oopassignment.repository.MemberRepository;
import oopassignment.repository.impl.InMemoryMemberRepository;
import oopassignment.service.MemberService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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

    @Test
    public void deductCreditDecreasesBalance() {
        MemberRecord member = memberService.registerMember("Deduct User", "030303-03-4444", 100.0);
        memberService.deductCredit(member.getMemberId(), 30.0);
        MemberRecord updated = memberService.findById(member.getMemberId()).orElseThrow();
        assertEquals("Deduction should decrease balance", 70.0, updated.getCreditBalance(), 0.0001);
    }

    @Test
    public void deductMoreThanBalanceThrows() {
        MemberRecord member = memberService.registerMember("Broke User", "040404-04-5555", 20.0);
        assertThrows("Should not allow deducting more than balance",
                InvalidInputException.class,
                () -> memberService.deductCredit(member.getMemberId(), 50.0));
    }

    @Test
    public void deductNegativeAmountThrows() {
        MemberRecord member = memberService.registerMember("Test User", "050505-05-6666", 50.0);
        assertThrows("Negative deduction should be rejected",
                InvalidInputException.class,
                () -> memberService.deductCredit(member.getMemberId(), -10.0));
    }

    @Test
    public void deductZeroAmountThrows() {
        MemberRecord member = memberService.registerMember("Zero User", "060606-06-7777", 50.0);
        assertThrows("Zero deduction should be rejected",
                InvalidInputException.class,
                () -> memberService.deductCredit(member.getMemberId(), 0.0));
    }

    @Test
    public void topUpNonExistentMemberThrows() {
        assertThrows("Should reject top up for non-existent member",
                EntityNotFoundException.class,
                () -> memberService.topUpCredit("MB999", 100.0));
    }

    @Test
    public void deductFromNonExistentMemberThrows() {
        assertThrows("Should reject deduction for non-existent member",
                EntityNotFoundException.class,
                () -> memberService.deductCredit("MB999", 50.0));
    }

    @Test
    public void findByIcReturnsCorrectMember() {
        MemberRecord member = memberService.registerMember("IC Find", "070707-07-8888", 25.0);
        Optional<MemberRecord> found = memberService.findByIc("070707-07-8888");
        assertTrue("Should find member by IC", found.isPresent());
        assertEquals("Member ID should match", member.getMemberId(), found.get().getMemberId());
    }

    @Test
    public void findByIcWithDifferentFormats() {
        memberService.registerMember("Format Test", "080808-08-9999", 10.0);
        Optional<MemberRecord> found = memberService.findByIc("080808089999");
        assertTrue("Should find member with IC without dashes", found.isPresent());
    }

    @Test
    public void searchByNameReturnsMatchingMembers() {
        memberService.registerMember("John Doe", "111111-11-1111", 0);
        memberService.registerMember("John Smith", "222222-22-2222", 0);
        memberService.registerMember("Jane Doe", "333333-33-3333", 0);
        
        List<MemberRecord> results = memberService.searchByName("John");
        assertEquals("Should find 2 members with 'John'", 2, results.size());
    }

    @Test
    public void searchByNameCaseInsensitive() {
        memberService.registerMember("Alice Wonderland", "444444-44-4444", 0);
        List<MemberRecord> results = memberService.searchByName("alice");
        assertFalse("Should find member regardless of case", results.isEmpty());
    }

    @Test
    public void findAllReturnsAllMembers() {
        int initialCount = memberService.findAll().size();
        memberService.registerMember("Member A", "555555-55-5555", 0);
        memberService.registerMember("Member B", "666666-66-6666", 0);
        
        List<MemberRecord> all = memberService.findAll();
        assertEquals("Should return all members", initialCount + 2, all.size());
    }

    @Test
    public void isValidIcAcceptsValidFormats() {
        assertTrue("Should accept full format", memberService.isValidIc("900101-01-1234"));
        assertTrue("Should accept without dashes", memberService.isValidIc("900101011234"));
    }

    @Test
    public void isValidIcRejectsInvalidFormats() {
        assertFalse("Should reject short IC", memberService.isValidIc("1234"));
        assertFalse("Should reject letters", memberService.isValidIc("ABC123"));
        assertFalse("Should reject null", memberService.isValidIc(null));
    }

    @Test
    public void maskIcHidesLastDigits() {
        String masked = memberService.maskIc("900101-01-1234");
        assertTrue("Masked IC should contain asterisks", masked.contains("****"));
        assertTrue("Masked IC should show first 6 digits", masked.startsWith("900101"));
    }

    @Test
    public void registerMemberWithNegativeCreditThrows() {
        assertThrows("Negative initial credit should be rejected",
                InvalidInputException.class,
                () -> memberService.registerMember("Negative", "777777-77-7777", -10.0));
    }

    @Test
    public void registerMemberWithBlankNameThrows() {
        assertThrows("Blank name should be rejected",
                InvalidInputException.class,
                () -> memberService.registerMember("", "888888-88-8888", 0));
    }

    @Test
    public void findByIdNormalizesInput() {
        MemberRecord member = memberService.registerMember("Normalize Test", "999999-99-9999", 0);
        Optional<MemberRecord> found = memberService.findById(" " + member.getMemberId().toLowerCase() + " ");
        assertTrue("Should find member with normalized ID", found.isPresent());
    }

    @Test
    public void findByIdReturnsEmptyForBlankId() {
        Optional<MemberRecord> found = memberService.findById("");
        assertFalse("Should return empty for blank ID", found.isPresent());
    }
}
