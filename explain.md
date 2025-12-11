# Modernisation & Refactoring Rationale

## Clean Code & Refactoring Justification
- **What was wrong (legacy):** UI classes mixed input, business rules, and file I/O (text files), leading to God classes, duplicate parsing, no test seams, and plaintext passwords. File paths were hardcoded (e.g., `D:\...\clothes.txt`) and logic was scattered.
- **Principles applied:** SRP (UI only handles input), DRY (central repositories/services), DI via `ApplicationContext`, layered architecture (UI → Service → Repository → DB), repository abstraction (JDBC vs in-memory), testability (pure services with predictable deps), immutable-ish domain records.
- **Before vs After snippets:**
  - **Auth:** `if (password.equals(storedPassword)) { /* login */ }` → `AuthService.login(...)` with `PasswordHasher.hash()` + lockout + role check.
  - **Employee:** UI read/write to `Manager.txt`/`Staff.txt` → `EmployeeService` + `EmployeeRepository` (`JdbcEmployeeRepository` / in-memory) and `calculateSalary` bonus logic isolated.
  - **Inventory/Order:** Manual loops over text files to check stock → `InventoryService.isStockAvailable` + `StockRepository` and `OrderService.placeOrder` coordinating stock + transaction save.
  - **Member:** Inline IC checks and duplicates via map scan → `MemberService.isValidIc`, duplicate check in `MemberRepository`, masking via `maskIc` for display.

## Architecture Choices (Why)
- UI, service, repository separation improves maintainability and swap-ability (DB vs in-memory) without touching UI.
- SQLite via repositories gives integrity and consistency over text files; in-memory repos keep tests fast.
- Domain packages make business concepts explicit (auth/member/product/order/report), reducing coupling.
- `ApplicationContext` centralises wiring/DI to avoid ad hoc instantiation and to minimise merge conflicts.

## Legacy vs Modernised
| Aspect | Legacy | Modernised |
| --- | --- | --- |
| Storage | Text files (`Manager.txt`, `clothes.txt`) | SQLite via `Database` + repos; in-memory fallback |
| Architecture | UI mixed with file I/O and business logic | Layered UI → Service → Repository |
| Auth | Plaintext compare, no lockout | Hashed passwords, lockout, role check in `AuthService` |
| Employee | File CRUD, self/last-manager delete possible | `EmployeeService` enforces rules, bonus calc centralised |
| Inventory/Order | Manual file scans, no transaction concept | `InventoryService`, `OrderService` with stock check + transaction save |
| Member data | No IC masking/validation | `MemberService` regex validation + `maskIc` |
| Testability | None | Service-level tests using in-memory repos |

## Folder Guide (what each area does)
- `src/main/java/oopassignment/config` — env/discount/lockout/back-token/DB config (`AppConfig`) and schema bootstrap/seed/versioning (`Database`).
- `src/main/java/oopassignment/util` — cross-cutting helpers: `ApplicationContext` (wiring/DI), `PasswordHasher`, `ValidationUtils`, console helpers.
- `src/main/java/oopassignment/domain` — business records/enums by subdomain (`auth`, `member`, `product`, `order`, `report`).
- `src/main/java/oopassignment/repository` — abstractions for persistence; implementations in `repository/impl` (JDBC and in-memory) with SLF4J logging for DB ops.
- `src/main/java/oopassignment/service` — business rules and orchestration (Auth, Employee, Member, Product, Inventory, Order, Report, Pricing) using `ValidationUtils` and domain-specific exceptions.
- `src/main/java/oopassignment/ui` — console menus (`BootsDo`, `LoginConsole`, feature consoles) that call services only.
- `src/test/java/oopassignment` — JUnit 4 tests for each service.
- Root: `lib/` JDBC + logging jars, `out/` compiled classes, `bootsdo.db` runtime SQLite file, `README.md`, `explain.md`.

## Testing Strategy & Results
- **Approach:** JUnit 4 tests targeting service logic (fast, isolated). DB swapped for in-memory by default for tests.
- **Why:** Covers critical flows (auth, salary, stock, orders, reporting) to prevent regressions in business rules.
- **Modules & Tests:**
  | Module | Tests | Purpose |
  | --- | --- | --- |
  | Auth | `AuthServiceTest` | Valid/invalid login, lockout behavior |
  | Employee | `EmployeeServiceTest` | Salary bonus calc, self-delete guard, last-manager block |
  | Member | `MemberServiceTest` | IC validation/duplicate/invalid, top-up rules |
  | Product | `ProductServiceTest` | Add product, prevent delete with stock, reject bad category |
  | Inventory | `InventoryServiceTest` | Availability check, reduce stock bounds |
  | Order | `OrderServiceTest` | Member discount, non-member flow, insufficient stock, totals, empty-order rejection |
  | Report | `ReportServiceTest` | Sales summary amount/count, member purchase history |
- **Tools:** JUnit 4.13.2 + Hamcrest; in-memory repos isolate logic. (No coverage tool configured; target is “services covered by automated JUnit tests.”)
- **Execution:** `java -ea -cp "<lib jars>:out" org.junit.runner.JUnitCore oopassignment.AuthServiceTest ... oopassignment.ReportServiceTest` (see README for exact command).

## Security & Ethical Data Handling
- Passwords hashed via `PasswordHasher` (no plaintext compare); lockout after repeated failures (configurable in `AppConfig`).
- Member IC validated (regex) and masked for display (`maskIc`), reducing leakage of personal identifiers.
- Structured DB schema replaces loose text files, improving data integrity and reducing accidental corruption; `schema_version` recorded for migration readiness.
- Employee safeguards: cannot delete own account; cannot delete last manager.
- Input validation on amounts/quantities/categories to avoid negative or invalid operations; SLF4J logging on service + repo actions for traceability.

## Team Contributions (example template)
- Member A — Auth & Employee module
- Member B — Member module
- Member C — Product & Inventory module
- Member D — Order & Reporting module

## Collaboration & Standards
- Shared standards: no business logic in UI, no direct DB/file calls in services, SRP/DRY naming, constants centralised (`AppConfig`), validation via `ValidationUtils`, and domain exceptions over generic throws.
- Dependency wiring centralised in `ApplicationContext` to avoid merge conflicts and ease swapping repos.
- Code reviews/checks around service methods and tests to ensure every new method has coverage and emits meaningful logs.
