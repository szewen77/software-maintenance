# Legacy vs Modernised System (Boots.Do)

## Overview
- **Legacy:** Console menus mixed business rules, file I/O, and display logic. Data lived in scattered text files with hardcoded absolute paths. No tests or clear separation of concerns.
- **Modern:** Layered design (UI → Service → Repository → DB). SQLite-backed repositories (with in-memory fallbacks) and isolated services for auth, employees, members, products, inventory, orders, and reporting.

## Architecture
| Aspect | Legacy | Modern |
| --- | --- | --- |
| Layers | Single-layer UI containing logic + I/O | UI (Scanner) only → Services → Repositories → DB |
| Wiring | Ad hoc `new` inside UI classes | Centralised in `util/ApplicationContext` (DI-style) |
| Domain | Implicit maps/arrays | Explicit records/enums in `domain/*` |
| Storage | Multiple `.txt` files with manual parsing | SQLite (`bootsdo.db`) via `repository.impl.Jdbc*`; in-memory implementations for tests; `schema_version` table tracks migrations |
| Config | Hardcoded paths/strings | `AppConfig` centralises DB URL, env flag, lockout threshold/duration, discount, menu back token, allowed categories |
| Logging | Console prints or silent failures | SLF4J logging across services and JDBC repos (info on writes, errors on DB issues) |
| Validation | Ad hoc/null checks | `ValidationUtils` for non-blank/positive/salary/quantity/category/role transition; domain-specific exceptions |

### Code contrast (UI doing everything vs service call)
**Before (legacy Order)**:
```java
// inside legacy Order UI
while((line = br.readLine()) != null) {
    String[] data = line.split(";");
    if(data[0].equals(productId) && data[1].equals(size)) {
        qty = Integer.parseInt(data[2]);
    }
}
if(qty < requestQty) { System.out.println("No stock"); }
```
**After (InventoryService + repo)**:
```java
// UI delegates
boolean ok = inventoryService.isStockAvailable(productId, size, qty);
// Service uses repository abstraction
public boolean isStockAvailable(String id, String size, int qty) {
    return stockRepository.getQuantity(id, size) >= qty;
}
```

## Auth & Employee Management
- **Legacy:** Plaintext password compare in UI; roles inferred manually; could delete last manager or self; no consistent reporting chain.
- **Modern:** `AuthService` hashes passwords (`PasswordHasher`), lockout after 3 failures, role check via `hasRole`. `EmployeeService` enforces “no self-deactivate” and “cannot delete last manager”, with salary bonus calc centralised. Managers only can open `EmployeeConsole`; staff created by a manager automatically inherit that manager as `upline_id`, managers have `upline_id = null`, and role changes auto-clear upline when promoted to manager.
**Before (plaintext check):**
```java
if(inputPass.equals(storedPass)) { login(); }
```
**After (hash + lockout):**
```java
AuthResult r = authService.login(user, pass);
if(r.isLocked()) { /* show wait message */ }
```

## Menus & Input UX
- **Legacy:** Mixed prompt logic and ad hoc `Scanner` parsing across UIs.
- **Modern:** Shared `BootsDo` input helpers (`readIntInRange`, `readDouble`, `readYesNo`, etc.) reduce crashy inputs; manager main menu removes Order option (manager focuses on admin), staff menu keeps checkout first. `OrderConsole` exposes `showMenu()` alias for menu routing.

## Member & Customer
- **Legacy:** IC format unchecked, duplicates allowed, IC printed in full.
- **Modern:** `MemberService.isValidIc` regex, duplicate checks in `MemberRepository`, `maskIc` when displaying, top-up rejects negatives, separate `CustomerService` for walk-ins.

## Product & Inventory
- **Legacy:** Hardcoded file paths (e.g., `D:\\tarc sem 4\\clothes.txt`); manual loops for stock.
- **Modern:** `ProductRepository` + `StockRepository` (JDBC/in-memory) with `InventoryService` coordinating availability and updates; prevents deleting products with stock.

## Orders & Reporting
- **Legacy:** Order logic intertwined with input; no transaction concept; reports read `Transaction.txt` manually.
- **Modern:** `OrderService` builds a transaction (header + items), calls `InventoryService` for stock, `PricingService` for discounts, `TransactionRepository` to persist. `ReportService` produces summaries and member history via repositories.
**Before (monolithic flow):**
```java
// UI: validate, compute totals, mutate stock file, print receipt in one method
```
**After (composed services):**
```java
OrderResult r = orderService.placeOrder(request); // stock check + save + totals
reportService.getTotalSales(from, to);            // repository-backed
```

## Reporting & Hierarchy Views
- **Legacy:** Reports were file scans; no notion of staff/upline relationship.
- **Modern:** `ReportService` uses repositories for sales/member history; added “Staff by manager” console report that groups staff by `upline_id` for easy hierarchy visibility.

## Data & Security
- **Legacy:** Plaintext passwords; no integrity; PII (IC) printed unmasked.
- **Modern:** Hashing + lockout; SQLite schema (`Database` bootstraps tables + `schema_version`); IC validation/masking; input validation on salary/credit/qty/category; controlled employee deactivation rules; SLF4J logging for auditability.

## Testing & Maintainability
- **Legacy:** No automated tests; changes risky.
- **Modern:** Assert-based tests per service using in-memory repos (`src/test/java/oopassignment/*Test.java`) covering auth, employee salary/rules, member validation, inventory, product deletion guard, order totals/discounts/stock, report summaries.

## How to see the difference
- Modern code paths:
  - UI: `src/main/java/oopassignment/ui/`
  - Services: `src/main/java/oopassignment/service/`
  - Repos: `src/main/java/oopassignment/repository/` + `repository/impl/`
  - Domain records/enums: `src/main/java/oopassignment/domain/`
  - Wiring: `src/main/java/oopassignment/util/ApplicationContext.java`
  - DB setup: `src/main/java/oopassignment/config/Database.java`
