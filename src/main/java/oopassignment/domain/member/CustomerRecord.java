package oopassignment.domain.member;

import java.time.LocalDate;

public class CustomerRecord {
    private String customerId;
    private String name;
    private LocalDate registeredDate;
    private LocalDate lastPurchaseDate;

    public CustomerRecord(String customerId, String name, LocalDate registeredDate, LocalDate lastPurchaseDate) {
        this.customerId = customerId;
        this.name = name;
        this.registeredDate = registeredDate;
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public LocalDate getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastPurchaseDate(LocalDate lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }
}
