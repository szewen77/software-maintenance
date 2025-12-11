package oopassignment;

import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.Role;
import oopassignment.exception.DuplicateEntityException;
import oopassignment.exception.InvalidInputException;
import oopassignment.exception.UnauthorizedActionException;
import oopassignment.repository.EmployeeRepository;
import oopassignment.repository.impl.InMemoryEmployeeRepository;
import oopassignment.service.EmployeeService;
import oopassignment.util.PasswordHasher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class EmployeeServiceTest {

    private EmployeeService employeeService;
    private EmployeeRepository repository;
    private PasswordHasher hasher;

    @Before
    public void setUp() {
        hasher = new PasswordHasher();
        repository = new InMemoryEmployeeRepository(hasher);
        employeeService = new EmployeeService(repository, hasher);
    }

    @Test
    public void managerSalaryHasBonus() {
        EmployeeRecord manager = employeeService.registerEmployee("manager2", "pw", Role.MANAGER, 5000.0, null);
        double salary = employeeService.calculateSalary(manager);
        assertEquals("Manager salary should include 20% bonus", 6000.0, salary, 0.0001);
    }

    @Test
    public void staffSalaryHasBonus() {
        EmployeeRecord staff = employeeService.registerEmployee("staff2", "pw", Role.STAFF, 3000.0, null);
        double salary = employeeService.calculateSalary(staff);
        assertEquals("Staff salary should include 10% bonus", 3300.0, salary, 0.0001);
    }

    @Test
    public void cannotDeleteOwnAccount() {
        EmployeeRecord manager = employeeService.findByUsername("manager").orElseThrow();
        assertThrows("Should not allow deactivating own account",
                UnauthorizedActionException.class,
                () -> employeeService.deactivateEmployee(manager.getId(), manager.getId()));
    }

    @Test
    public void cannotDeleteLastManager() {
        EmployeeRecord manager = employeeService.findByUsername("manager").orElseThrow();
        assertThrows("Should not allow deleting the last manager",
                UnauthorizedActionException.class,
                () -> employeeService.deactivateEmployee(manager.getId(), "S001"));
    }

    @Test
    public void registerEmployeeRejectsDuplicateUsername() {
        assertThrows("Duplicate username should be rejected",
                DuplicateEntityException.class,
                () -> employeeService.registerEmployee("manager", "pw", Role.MANAGER, 4000.0, null));
    }

    @Test
    public void registerEmployeeValidatesSalary() {
        assertThrows("Zero salary should be invalid",
                InvalidInputException.class,
                () -> employeeService.registerEmployee("newbie", "pw", Role.STAFF, 0, null));
    }

    @Test
    public void modifyEmployeeUpdatesRoleSalaryAndBonus() {
        EmployeeRecord staff = employeeService.registerEmployee("rolechange", "pw", Role.STAFF, 2000.0, "M001");
        EmployeeRecord modified = employeeService.modifyEmployee(staff.getId(), null, Role.MANAGER, 3500.0, null);

        assertEquals("Role should update to manager", Role.MANAGER, modified.getRole());
        assertEquals("Base salary should update", 3500.0, modified.getBaseSalary(), 0.0001);
        assertEquals("Manager bonus should apply", 0.20, modified.getBonusRate(), 0.0001);
        assertTrue("Manager should not keep an upline", modified.getUplineId() == null || modified.getUplineId().isEmpty());
    }

    @Test
    public void updatePasswordHashesNewValue() {
        EmployeeRecord staff = employeeService.registerEmployee("changepw", "oldpw", Role.STAFF, 1800.0, "M001");
        String oldHash = staff.getPasswordHash();

        employeeService.updatePassword(staff.getId(), "newpw");
        EmployeeRecord refreshed = repository.findById(staff.getId()).orElseThrow();

        assertNotEquals("Password hash should change", oldHash, refreshed.getPasswordHash());
        assertEquals("Hash should match hasher output", hasher.hash("newpw"), refreshed.getPasswordHash());
    }

    @Test
    public void findByIdNormalizesInput() {
        assertTrue("Existing ID should be found regardless of casing",
                employeeService.findById(" m001 ").isPresent());
    }
}
