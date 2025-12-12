package oopassignment.util;

import oopassignment.repository.EmployeeRepository;
import oopassignment.service.EmployeeService;
import oopassignment.service.AuthService;
import oopassignment.repository.MemberRepository;
import oopassignment.service.MemberService;
import oopassignment.config.Database;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.StockRepository;
import oopassignment.service.ProductService;
import oopassignment.service.InventoryService;
import oopassignment.repository.TransactionRepository;
import oopassignment.repository.impl.InMemoryEmployeeRepository;
import oopassignment.repository.impl.JdbcEmployeeRepository;
import oopassignment.service.PricingService;
import oopassignment.service.OrderService;
import oopassignment.service.ReportService;
import oopassignment.repository.impl.InMemoryMemberRepository;
import oopassignment.repository.impl.JdbcMemberRepository;
import oopassignment.repository.impl.InMemoryProductRepository;
import oopassignment.repository.impl.JdbcProductRepository;
import oopassignment.repository.impl.InMemoryStockRepository;
import oopassignment.repository.impl.JdbcStockRepository;
import oopassignment.repository.impl.InMemoryTransactionRepository;
import oopassignment.repository.impl.JdbcTransactionRepository;

public final class ApplicationContext {

    public static final PasswordHasher PASSWORD_HASHER = new PasswordHasher();

    public static final EmployeeRepository EMPLOYEE_REPOSITORY = chooseEmployeeRepository();
    public static final EmployeeService EMPLOYEE_SERVICE = new EmployeeService(EMPLOYEE_REPOSITORY, PASSWORD_HASHER);
    public static final AuthService AUTH_SERVICE = new AuthService(EMPLOYEE_REPOSITORY, PASSWORD_HASHER);

    public static final MemberRepository MEMBER_REPOSITORY = chooseMemberRepository();
    public static final MemberService MEMBER_SERVICE = new MemberService(MEMBER_REPOSITORY);

    public static final ProductRepository PRODUCT_REPOSITORY = chooseProductRepository();
    public static final StockRepository STOCK_REPOSITORY = chooseStockRepository();
    public static final ProductService PRODUCT_SERVICE = new ProductService(PRODUCT_REPOSITORY, STOCK_REPOSITORY);
    public static final InventoryService INVENTORY_SERVICE = new InventoryService(STOCK_REPOSITORY);

    public static final TransactionRepository TRANSACTION_REPOSITORY = chooseTransactionRepository();
    public static final PricingService PRICING_SERVICE = new PricingService();
    public static final OrderService ORDER_SERVICE = new OrderService(PRODUCT_REPOSITORY, INVENTORY_SERVICE, PRICING_SERVICE, TRANSACTION_REPOSITORY, MEMBER_REPOSITORY);
    public static final ReportService REPORT_SERVICE = new ReportService(TRANSACTION_REPOSITORY);

    private ApplicationContext() {
    }

    private static EmployeeRepository chooseEmployeeRepository() {
        if (Database.isAvailable()) {
            return new JdbcEmployeeRepository();
        }
        return new InMemoryEmployeeRepository(PASSWORD_HASHER);
    }

    private static MemberRepository chooseMemberRepository() {
        if (Database.isAvailable()) {
            return new JdbcMemberRepository();
        }
        return new InMemoryMemberRepository();
    }

    private static ProductRepository chooseProductRepository() {
        if (Database.isAvailable()) {
            return new JdbcProductRepository();
        }
        return new InMemoryProductRepository();
    }

    private static StockRepository chooseStockRepository() {
        if (Database.isAvailable()) {
            return new JdbcStockRepository();
        }
        return new InMemoryStockRepository();
    }

    private static TransactionRepository chooseTransactionRepository() {
        if (Database.isAvailable()) {
            return new JdbcTransactionRepository();
        }
        return new InMemoryTransactionRepository();
    }
}
