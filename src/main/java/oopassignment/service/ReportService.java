package oopassignment.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import oopassignment.domain.order.TransactionHeader;
import oopassignment.domain.report.MemberPurchase;
import oopassignment.domain.report.SalesSummary;
import oopassignment.repository.TransactionRepository;

public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public SalesSummary getTotalSales(LocalDate from, LocalDate to) {
        double total = 0;
        int count = 0;
        for (TransactionHeader header : transactionRepository.findAllHeaders()) {
            LocalDate date = header.getDateTime().toLocalDate();
            if ((from == null || !date.isBefore(from)) && (to == null || !date.isAfter(to))) {
                total += header.getTotalAmount();
                count++;
            }
        }
        return new SalesSummary(total, count);
    }

    public SalesSummary getSalesSummary(LocalDate from, LocalDate to) {
        return getTotalSales(from, to);
    }

    public List<MemberPurchase> getMemberPurchaseHistory(String memberId) {
        List<MemberPurchase> purchases = new ArrayList<>();
        for (TransactionHeader header : transactionRepository.findAllHeaders()) {
            if (memberId != null && memberId.equals(header.getMemberId())) {
                purchases.add(new MemberPurchase(
                        header.getTransactionId(),
                        header.getDateTime(),
                        header.getTotalAmount(),
                        header.getPaymentMethod()));
            }
        }
        return purchases;
    }

    public List<TransactionHeader> getTransactionsInRange(LocalDate from, LocalDate to) {
        List<TransactionHeader> transactions = new ArrayList<>();
        for (TransactionHeader header : transactionRepository.findAllHeaders()) {
            LocalDate date = header.getDateTime().toLocalDate();
            if ((from == null || !date.isBefore(from)) && (to == null || !date.isAfter(to))) {
                transactions.add(header);
            }
        }
        return transactions;
    }
}
