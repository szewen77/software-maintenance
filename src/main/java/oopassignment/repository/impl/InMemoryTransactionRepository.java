package oopassignment.repository.impl;
import oopassignment.*;
import oopassignment.domain.auth.*;
import oopassignment.domain.member.*;
import oopassignment.domain.product.*;
import oopassignment.domain.order.*;
import oopassignment.repository.*;
import oopassignment.util.*;
import oopassignment.config.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {

    private final List<TransactionHeader> headers = new ArrayList<>();
    private final List<TransactionItem> items = new ArrayList<>();

    @Override
    public void saveTransaction(TransactionHeader header, List<TransactionItem> transactionItems) {
        headers.add(header);
        items.addAll(transactionItems);
    }

    @Override
    public List<TransactionHeader> findAllHeaders() {
        return new ArrayList<>(headers);
    }

    @Override
    public List<TransactionItem> findItemsByTransaction(String transactionId) {
        return items.stream()
                .filter(i -> i.getTransactionId().equals(transactionId))
                .collect(Collectors.toList());
    }
}
