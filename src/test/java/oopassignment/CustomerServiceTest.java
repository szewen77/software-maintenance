package oopassignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import oopassignment.domain.member.CustomerRecord;
import oopassignment.repository.CustomerRepository;
import oopassignment.repository.impl.InMemoryCustomerRepository;
import oopassignment.service.CustomerService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

public class CustomerServiceTest {

    private CustomerService customerService;

    @Before
    public void setUp() {
        CustomerRepository repository = new InMemoryCustomerRepository();
        customerService = new CustomerService(repository);
    }

    @Test
    public void registerCustomerGeneratesId() {
        CustomerRecord customer = customerService.registerCustomer("John Doe");
        assertNotNull("Customer ID should be generated", customer.getCustomerId());
        assertTrue("Customer ID should start with CU", customer.getCustomerId().startsWith("CU"));
    }

    @Test
    public void registerCustomerSetsRegistrationDate() {
        CustomerRecord customer = customerService.registerCustomer("Jane Smith");
        assertEquals("Registration date should be today", LocalDate.now(), customer.getRegisteredDate());
    }

    @Test
    public void findByIdReturnsCorrectCustomer() {
        CustomerRecord customer = customerService.registerCustomer("Find Test");
        Optional<CustomerRecord> found = customerService.findById(customer.getCustomerId());
        assertTrue("Should find customer by ID", found.isPresent());
        assertEquals("Customer name should match", "Find Test", found.get().getName());
    }

    @Test
    public void findByIdNormalizesInput() {
        CustomerRecord customer = customerService.registerCustomer("Normalize Test");
        Optional<CustomerRecord> found = customerService.findById(" " + customer.getCustomerId().toLowerCase() + " ");
        assertTrue("Should find customer with normalized ID", found.isPresent());
    }

    @Test
    public void findByIdReturnsEmptyForBlankId() {
        Optional<CustomerRecord> found = customerService.findById("");
        assertFalse("Should return empty for blank ID", found.isPresent());
    }

    @Test
    public void findByIdReturnsEmptyForNullId() {
        Optional<CustomerRecord> found = customerService.findById(null);
        assertFalse("Should return empty for null ID", found.isPresent());
    }

    @Test
    public void findByIdReturnsEmptyForNonExistent() {
        Optional<CustomerRecord> found = customerService.findById("CU999");
        assertFalse("Should return empty for non-existent ID", found.isPresent());
    }

    @Test
    public void findAllReturnsAllCustomers() {
        int initialCount = customerService.findAll().size();
        customerService.registerCustomer("Customer A");
        customerService.registerCustomer("Customer B");
        customerService.registerCustomer("Customer C");
        
        List<CustomerRecord> all = customerService.findAll();
        assertEquals("Should return all customers", initialCount + 3, all.size());
    }

    @Test
    public void updateLastPurchaseUpdatesDate() {
        CustomerRecord customer = customerService.registerCustomer("Purchase Test");
        LocalDate purchaseDate = LocalDate.now().minusDays(5);
        
        customerService.updateLastPurchase(customer.getCustomerId(), purchaseDate);
        
        CustomerRecord updated = customerService.findById(customer.getCustomerId()).orElseThrow();
        assertEquals("Last purchase date should be updated", purchaseDate, updated.getLastPurchaseDate());
    }

    @Test
    public void updateLastPurchaseForNonExistentThrows() {
        assertThrows("Should throw for non-existent customer",
                IllegalArgumentException.class,
                () -> customerService.updateLastPurchase("CU999", LocalDate.now()));
    }

    @Test
    public void multipleRegistrationsHaveUniqueIds() {
        CustomerRecord c1 = customerService.registerCustomer("Customer 1");
        CustomerRecord c2 = customerService.registerCustomer("Customer 2");
        CustomerRecord c3 = customerService.registerCustomer("Customer 3");
        
        assertFalse("Customer IDs should be unique", c1.getCustomerId().equals(c2.getCustomerId()));
        assertFalse("Customer IDs should be unique", c2.getCustomerId().equals(c3.getCustomerId()));
        assertFalse("Customer IDs should be unique", c1.getCustomerId().equals(c3.getCustomerId()));
    }

    @Test
    public void customerIdsAreSequential() {
        CustomerRecord c1 = customerService.registerCustomer("Sequential 1");
        CustomerRecord c2 = customerService.registerCustomer("Sequential 2");
        
        int id1 = Integer.parseInt(c1.getCustomerId().substring(2));
        int id2 = Integer.parseInt(c2.getCustomerId().substring(2));
        
        assertEquals("IDs should be sequential", id1 + 1, id2);
    }
}

