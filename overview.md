# Project Overview (Boots.Do)

This document summarises the modernised console system and how it differs from the legacy approach.

## Architecture at a Glance
- Layered flow: UI (Scanner consoles) → Services → Repositories → SQLite DB.
- Wiring: centralised in `src/main/java/oopassignment/util/ApplicationContext.java`.
- Storage: SQLite (`bootsdo.db`) via JDBC repositories, with in-memory fallbacks for tests.
- Domains: auth, employees, members, products/stock, orders, reports (`src/main/java/oopassignment/domain/*`).

## Legacy vs Modern Highlights
- **Legacy:** UI mixed business rules and file I/O, scattered text files with hardcoded paths, no tests, plaintext passwords, ad hoc role handling.
- **Modern:** Layered design, hashed auth with lockout, role checks, protected employee actions (cannot delete last manager or self), validation for IC/credit/salary/qty, and consistent repository-backed data.

### Auth & Employee Management
- Legacy: plaintext password compare; roles inferred manually; could delete last manager or self; no consistent reporting chain.
- Modern: `AuthService` hashes passwords and enforces lockout; `EmployeeService` centralises bonuses and safety rules. Only managers can open `EmployeeConsole`; staff created by a manager auto-inherit that manager as `upline_id`; managers have `upline_id = null`; promotions to manager clear upline. Reporting menu includes a “Staff by manager” view.

### Menus & Input UX
- Shared input helpers in `BootsDo` (`readIntInRange`, `readDouble`, `readYesNo`, etc.) to avoid crashy `Scanner` parsing.
- Manager main menu drops Order (admin focus); staff menu keeps Checkout first. `OrderConsole.showMenu()` is available for routing.

### Member & Customer
- IC validation/masking, duplicate checks in `MemberRepository`, negative top-ups rejected; `CustomerService` handles walk-ins.

### Product & Inventory
- Guard against deleting products that still have stock; stock checks and updates go through `InventoryService` + repositories instead of file loops.

### Orders & Reporting
- Orders: `OrderService` composes inventory checks, pricing, and persistence; `OrderResult` returned to UI.
- Reporting: `ReportService` supplies sales summary, member purchase history, and staff-by-manager grouping via DB-backed queries.

### Data & Security
- SQLite schema bootstrapped in `src/main/java/oopassignment/config/Database.java`; passwords hashed; IC masked; input validated (salary/credit/qty).

### Testing & Maintainability
- JUnit 4 service tests in `src/test/java/oopassignment/*Test.java` using in-memory repositories cover auth, employee salary/rules, member validation, inventory, product deletion guard, order totals/discounts/stock, and report summaries.

## How to Build & Run
- Compile:  
  `javac -cp "lib/sqlite-jdbc-3.45.3.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar:src/main/java" -d out $(find src/main/java -name '*.java') $(find src/test/java -name '*.java')`
- Run:  
  `java -cp "lib/sqlite-jdbc-3.45.3.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar:out" oopassignment.ui.BootsDo`
- Tests (JUnit 4):  
  `java -ea -cp "lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:lib/sqlite-jdbc-3.45.3.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar:out" org.junit.runner.JUnitCore oopassignment.AuthServiceTest oopassignment.EmployeeServiceTest oopassignment.MemberServiceTest oopassignment.InventoryServiceTest oopassignment.ProductServiceTest oopassignment.OrderServiceTest oopassignment.ReportServiceTest`

## Recent Enhancements for the Rubric
- Maintainability & Architecture: central `ValidationUtils` (non-blank, positive/non-negative, salary/quantity/category, role transition) plus domain exceptions (`InvalidInput/EntityNotFound/InsufficientStock/DuplicateEntity/UnauthorizedAction`) replace generic throws.
- Design Choices & Config: `AppConfig` now holds lockout/discountrate/back-token/environment/category constants; UI uses `BACK_TOKEN` for cancel flows; DI still centralised in `ApplicationContext`.
- Data Stewardship & Security: services/repos use SLF4J logging instead of prints (including info logs on DB writes); JDBC transaction saves log + rollback; `Database` bootstraps a `schema_version` table and records applied version.
- UX & Resilience: product/employee delete/search accept the shared back token instead of trapping users; validation errors surface friendly messages without crashing.
- Testing Strategy: negative cases cover invalid IC, duplicate IC, invalid category, empty order, over-stock orders, delete-last-manager, self-delete, and stock depletion errors alongside the positive flows.
