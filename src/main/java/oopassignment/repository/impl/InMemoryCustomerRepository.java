package oopassignment.repository.impl;
import oopassignment.*;
import oopassignment.domain.auth.*;
import oopassignment.domain.member.*;
import oopassignment.domain.product.*;
import oopassignment.domain.order.*;
import oopassignment.repository.*;
import oopassignment.util.*;
import oopassignment.config.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<String, CustomerRecord> customers = new LinkedHashMap<>();

    public InMemoryCustomerRepository() {
        seedDefaults();
    }

    private void seedDefaults() {
        CustomerRecord walkIn = new CustomerRecord(
                "CU001",
                "Walk-in",
                LocalDate.now().minusDays(1),
                null
        );
        save(walkIn);
    }

    @Override
    public Optional<CustomerRecord> findById(String customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    @Override
    public void save(CustomerRecord customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    @Override
    public void update(CustomerRecord customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    @Override
    public List<CustomerRecord> findAll() {
        return new ArrayList<>(customers.values());
    }
}
