package oopassignment.repository;

import java.util.List;
import oopassignment.domain.order.TransactionHeader;
import oopassignment.domain.order.TransactionItem;

public interface TransactionRepository {
    void saveTransaction(TransactionHeader header, List<TransactionItem> items);

    List<TransactionHeader> findAllHeaders();

    List<TransactionItem> findItemsByTransaction(String transactionId);
}
