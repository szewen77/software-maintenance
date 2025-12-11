# Boots.Do Console App (Java)

This project is a console-based retail/order system with a simple layered design: UI -> Services -> Repositories -> DB (SQLite) or in-memory.

## Layout
```
OOP/
  lib/                        # Third-party jars (SQLite JDBC + SLF4J)
    sqlite-jdbc-3.45.3.0.jar
    slf4j-api-2.0.13.jar
    slf4j-simple-2.0.13.jar
    junit-4.13.2.jar
    hamcrest-core-1.3.jar
  src/
    main/java/oopassignment/
      config/                 # AppConfig (env/discount/lockout/back token/DB), Database (schema bootstrap + version)
      util/                   # ApplicationContext (wires services/repos), PasswordHasher, ValidationUtils
      domain/                 # Data records and enums grouped by domain
        auth/                 # Role, EmploymentStatus, EmployeeRecord
        member/               # MemberRecord, MemberStatus, CustomerRecord
        product/              # ProductRecord, ProductStatus, StockItem
        order/                # OrderItemRequest, OrderRequest, OrderResult, TransactionHeader/Item
        report/               # SalesSummary, MemberPurchase
      repository/             # Repository interfaces
        interfaces/           # (optional placeholder if you want to split interfaces later)
        impl/                 # JDBC and in-memory implementations (SLF4J logging on DB ops)
      service/                # AuthService, EmployeeService, MemberService, CustomerService,
                              # ProductService, InventoryService, OrderService, ReportService, PricingService
                              # (use ValidationUtils + domain exceptions)
      ui/                     # Console entry points (BootsDo, LoginConsole, *Console UIs)
    test/java/oopassignment/  # Assert-based tests for services and flows
  out/                        # Compiled classes (javac output)
  bootsdo.db                  # SQLite DB created at runtime
  README.md
```

### Key classes
- Entry point: `oopassignment.ui.BootsDo` (starts the app, shows ASCII logo, launches LoginConsole).
- UI layer: `LoginConsole`, `EmployeeConsole`, `MemberConsole`, `ProductConsole`, `OrderConsole`, `ReportConsole` (Scanner-based menus calling services).
- Services: business rules and orchestration in `service/`.
- Repositories: interfaces in `repository/`, implementations in `repository/impl/` (JDBC uses `Database` + SQLite; in-memory for tests/fallback).
- Domain models: immutable-ish records/enums under `domain/...` for auth, members, products, orders, reporting.
- Config/utility: `config/AppConfig.java` (DB URL), `config/Database.java` (creates tables + seeds), `util/ApplicationContext.java` (wiring), `util/PasswordHasher.java` (SHA-256 stub).

## Build & Run
Compile (main + tests):
```bash
javac -cp "lib/sqlite-jdbc-3.45.3.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:src/main/java" \
  -d out $(find src/main/java -name '*.java') $(find src/test/java -name '*.java')
```
Run app:
```bash
java -cp "lib/sqlite-jdbc-3.45.3.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar:out" oopassignment.ui.BootsDo
```
Tests (JUnit 4):
```bash
java -ea -cp "lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:lib/sqlite-jdbc-3.45.3.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar:out" \
  org.junit.runner.JUnitCore \
  oopassignment.AuthServiceTest \
  oopassignment.EmployeeServiceTest \
  oopassignment.MemberServiceTest \
  oopassignment.InventoryServiceTest \
  oopassignment.ProductServiceTest \
  oopassignment.OrderServiceTest \
  oopassignment.ReportServiceTest
```

## Configuration & Database
- `config/AppConfig.java`: env flag (`bootsdo.env`), member discount, lockout attempts/duration, back token for menus, allowed product categories, DB URL, and schema version.
- `config/Database.java`: auto-creates tables, seeds defaults, records `schema_version`, and rolls back on bootstrap errors. Uses SQLite at `jdbc:sqlite:bootsdo.db` (falls back to in-memory repos if driver/DB unavailable).

## Error Handling & Logging
- Domain-specific exceptions: `InvalidInputException`, `EntityNotFoundException`, `InsufficientStockException`, `DuplicateEntityException`, `UnauthorizedActionException`.
- Validation centralised in `util/ValidationUtils` (non-blank, positive/non-negative, salary/quantity/category, role transition).
- SLF4J logging in services and JDBC repositories for both success and error paths (requires `slf4j-simple` jar on classpath).

## Notes
- `ApplicationContext` selects JDBC repos when the DB is available; otherwise it uses in-memory implementations. Shared back token for console “return” flows comes from `AppConfig.BACK_TOKEN`.
