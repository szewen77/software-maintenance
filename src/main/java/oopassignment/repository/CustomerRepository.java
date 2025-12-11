package oopassignment.repository;

import java.util.List;
import java.util.Optional;
import oopassignment.domain.member.CustomerRecord;

public interface CustomerRepository {
    Optional<CustomerRecord> findById(String customerId);

    void save(CustomerRecord customer);

    void update(CustomerRecord customer);

    List<CustomerRecord> findAll();
}
