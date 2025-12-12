package oopassignment.domain.order;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest {
    private final String memberId;
    private final List<OrderItemRequest> items;
    private final String paymentMethod;

    public OrderRequest(String memberId, List<OrderItemRequest> items, String paymentMethod) {
        this.memberId = memberId;
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.paymentMethod = paymentMethod == null ? "CASH" : paymentMethod;
    }

    public String getMemberId() {
        return memberId;
    }

    public List<OrderItemRequest> getItems() {
        return new ArrayList<>(items);
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getCustomerType() {
        // Determine customer type based on memberId
        return (memberId != null && !memberId.isBlank()) ? "MEMBER" : "WALK-IN";
    }
}
