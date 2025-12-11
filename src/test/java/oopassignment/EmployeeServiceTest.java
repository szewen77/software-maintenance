package oopassignment;

import oopassignment.domain.auth.EmployeeRecord;
import oopassignment.domain.auth.Role;
import oopassignment.exception.UnauthorizedActionException;
import oopassignment.repository.EmployeeRepository;
import oopassignment.repository.impl.InMemoryEmployeeRepository;
import oopassignment.service.EmployeeService;
import oopassignment.util.PasswordHasher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class EmployeeServiceTest {

    private EmployeeService employeeService;
    private EmployeeRepository repository;

    @Before
    public void setUp() {
        PasswordHasher hasher = new PasswordHasher();
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
}
