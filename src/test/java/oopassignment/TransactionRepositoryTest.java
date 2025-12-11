package oopassignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import oopassignment.domain.order.TransactionHeader;
import oopassignment.domain.order.TransactionItem;
import oopassignment.repository.TransactionRepository;
import oopassignment.repository.impl.InMemoryTransactionRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransactionRepositoryTest {

    private TransactionRepository repo;

    @Before
    public void setUp() {
        repo = new InMemoryTransactionRepository();
    }

    @Test
    public void saveTransactionAndFind() {
        TransactionHeader header = new TransactionHeader("T999", LocalDateTime.now(),
                "MB001", "CU001", 95.0, "CASH");
        TransactionItem item = new TransactionItem("T999", 1, "P001", "M", 2, 47.5);
        
        repo.saveTransaction(header, List.of(item));
        
        List<TransactionHeader> headers = repo.findAllHeaders();
        boolean found = headers.stream()
                .anyMatch(h -> "T999".equals(h.getTransactionId()));
        assertTrue("Should find saved transaction", found);
    }

    @Test
    public void findItemsByTransactionId() {
        TransactionHeader header = new TransactionHeader("T998", LocalDateTime.now(),
                "MB002", "CU002", 200.0, "CARD");
        TransactionItem item1 = new TransactionItem("T998", 1, "P001", "M", 1, 19.90);
        TransactionItem item2 = new TransactionItem("T998", 2, "P002", "42", 1, 120.00);
        
        repo.saveTransaction(header, List.of(item1, item2));
        
        List<TransactionItem> items = repo.findItemsByTransaction("T998");
        assertEquals("Should have 2 items", 2, items.size());
    }

    @Test
    public void findAllHeaders() {
        int initialCount = repo.findAllHeaders().size();
        
        TransactionHeader h1 = new TransactionHeader("T997", LocalDateTime.now(),
                null, "CU003", 50.0, "CASH");
        TransactionHeader h2 = new TransactionHeader("T996", LocalDateTime.now(),
                "MB003", "CU004", 75.0, "CARD");
        
        repo.saveTransaction(h1, List.of());
        repo.saveTransaction(h2, List.of());
        
        List<TransactionHeader> all = repo.findAllHeaders();
        assertTrue("Should have added transactions", all.size() >= initialCount + 2);
    }

    @Test
    public void findItemsForNonExistentTransaction() {
        List<TransactionItem> items = repo.findItemsByTransaction("NONEXISTENT");
        assertTrue("Should return empty list", items.isEmpty());
    }

    @Test
    public void saveTransactionWithMultipleItems() {
        TransactionHeader header = new TransactionHeader("T992", LocalDateTime.now(),
                "MB005", "CU008", 300.0, "WALLET");
        TransactionItem i1 = new TransactionItem("T992", 1, "P001", "M", 2, 19.90);
        TransactionItem i2 = new TransactionItem("T992", 2, "P002", "42", 1, 120.00);
        TransactionItem i3 = new TransactionItem("T992", 3, "P001", "L", 3, 19.90);
        
        repo.saveTransaction(header, List.of(i1, i2, i3));
        
        List<TransactionItem> items = repo.findItemsByTransaction("T992");
        assertEquals("Should have 3 items", 3, items.size());
    }

    @Test
    public void transactionWithNoMemberId() {
        TransactionHeader header = new TransactionHeader("T991", LocalDateTime.now(),
                null, "CU-WALKIN", 45.0, "CASH");
        TransactionItem item = new TransactionItem("T991", 1, "P001", "M", 1, 19.90);
        
        repo.saveTransaction(header, List.of(item));
        
        List<TransactionHeader> headers = repo.findAllHeaders();
        Optional<TransactionHeader> found = headers.stream()
                .filter(h -> "T991".equals(h.getTransactionId()))
                .findFirst();
        
        assertTrue("Should save transaction without member", found.isPresent());
        assertNull("Member ID should be null", found.get().getMemberId());
    }

    @Test
    public void transactionWithWalletPayment() {
        TransactionHeader header = new TransactionHeader("T990", LocalDateTime.now(),
                "MB006", "CU009", 150.0, "WALLET");
        
        repo.saveTransaction(header, List.of());
        
        List<TransactionHeader> headers = repo.findAllHeaders();
        Optional<TransactionHeader> found = headers.stream()
                .filter(h -> "T990".equals(h.getTransactionId()))
                .findFirst();
        
        assertTrue("Should find wallet transaction", found.isPresent());
        assertEquals("WALLET", found.get().getPaymentMethod());
    }

    @Test
    public void transactionItemsPreserveOrder() {
        TransactionHeader header = new TransactionHeader("T988", LocalDateTime.now(),
                "MB008", "CU011", 200.0, "CARD");
        
        TransactionItem i1 = new TransactionItem("T988", 1, "P001", "M", 1, 19.90);
        TransactionItem i2 = new TransactionItem("T988", 2, "P002", "42", 1, 120.00);
        TransactionItem i3 = new TransactionItem("T988", 3, "P001", "L", 1, 19.90);
        
        repo.saveTransaction(header, List.of(i1, i2, i3));
        
        List<TransactionItem> items = repo.findItemsByTransaction("T988");
        assertEquals("First item should be line 1", 1, items.get(0).getLineNo());
        assertEquals("Second item should be line 2", 2, items.get(1).getLineNo());
        assertEquals("Third item should be line 3", 3, items.get(2).getLineNo());
    }

    @Test
    public void saveEmptyItemsList() {
        TransactionHeader header = new TransactionHeader("T987", LocalDateTime.now(),
                "MB009", "CU012", 0.0, "CASH");
        
        repo.saveTransaction(header, List.of());
        
        List<TransactionItem> items = repo.findItemsByTransaction("T987");
        assertTrue("Should handle empty items list", items.isEmpty());
    }

    @Test
    public void findAllHeadersReturnsNonNull() {
        List<TransactionHeader> headers = repo.findAllHeaders();
        assertNotNull("findAllHeaders should not return null", headers);
    }
}

