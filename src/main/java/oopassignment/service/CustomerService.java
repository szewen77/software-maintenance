package oopassignment.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import oopassignment.domain.member.CustomerRecord;
import oopassignment.repository.CustomerRepository;

public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerRecord registerCustomer(String name) {
        String id = generateId();
        CustomerRecord customer = new CustomerRecord(id, name, LocalDate.now(), null);
        repository.save(customer);
        return customer;
    }

    public Optional<CustomerRecord> findById(String id) {
        String normalized = normalizeId(id);
        if (normalized.isEmpty()) {
            return Optional.empty();
        }
        return repository.findById(normalized);
    }

    public List<CustomerRecord> findAll() {
        return repository.findAll();
    }

    public void updateLastPurchase(String customerId, LocalDate purchaseDate) {
        CustomerRecord record = repository.findById(normalizeId(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        record.setLastPurchaseDate(purchaseDate);
        repository.update(record);
    }

    private String generateId() {
        int max = 0;
        for (CustomerRecord record : repository.findAll()) {
            String id = record.getCustomerId();
            if (id != null && id.startsWith("CU")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignore) {
                    // skip malformed ids
                }
            }
        }
        return "CU" + String.format("%03d", max + 1);
    }

    private String normalizeId(String id) {
        if (id == null) {
            return "";
        }
        String trimmed = id.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.toUpperCase();
    }
}
