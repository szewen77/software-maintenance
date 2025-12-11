package oopassignment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import oopassignment.domain.order.OrderItemRequest;
import oopassignment.domain.order.OrderRequest;
import oopassignment.domain.order.OrderResult;
import oopassignment.domain.order.TransactionHeader;
import oopassignment.domain.order.TransactionItem;
import oopassignment.domain.product.ProductRecord;
import oopassignment.exception.EntityNotFoundException;
import oopassignment.exception.InsufficientStockException;
import oopassignment.exception.InvalidInputException;
import oopassignment.repository.ProductRepository;
import oopassignment.repository.TransactionRepository;
import oopassignment.repository.MemberRepository;
import oopassignment.domain.member.MemberRecord;
import oopassignment.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final PricingService pricingService;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    public OrderService(ProductRepository productRepository,
                        InventoryService inventoryService,
                        PricingService pricingService,
                        TransactionRepository transactionRepository,
                        MemberRepository memberRepository) {
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.pricingService = pricingService;
        this.transactionRepository = transactionRepository;
        this.memberRepository = memberRepository;
    }

    // Backward compatibility constructor (deprecated)
    @Deprecated
    public OrderService(ProductRepository productRepository,
                        InventoryService inventoryService,
                        PricingService pricingService,
                        TransactionRepository transactionRepository) {
        this(productRepository, inventoryService, pricingService, transactionRepository, null);
    }

    public OrderResult placeOrder(OrderRequest request) {
        if (request == null) {
            throw new InvalidInputException("Order request cannot be null");
        }
        if (request.getItems().isEmpty()) {
            throw new InvalidInputException("Order must contain at least one item");
        }
        String paymentMethod = ValidationUtils.requireNotBlank(request.getPaymentMethod(), "Payment method");

        boolean isMember = request.getMemberId() != null && !request.getMemberId().isBlank();

        double subtotal = 0;
        List<TransactionItem> transactionItems = new ArrayList<>();
        int lineNo = 1;
        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductRecord product = getProductOrThrow(itemRequest.getProductId());
            ValidationUtils.validateQuantity(itemRequest.getQuantity());
            if (!inventoryService.isStockAvailable(itemRequest.getProductId(), itemRequest.getSize(), itemRequest.getQuantity())) {
                throw new InsufficientStockException("Insufficient stock for " + product.getProductId());
            }
            subtotal += product.getPrice() * itemRequest.getQuantity();
            transactionItems.add(new TransactionItem(
                    "",
                    lineNo++,
                    itemRequest.getProductId(),
                    itemRequest.getSize(),
                    itemRequest.getQuantity(),
                    product.getPrice()
            ));
        }

        double total = pricingService.applyMemberDiscount(subtotal, isMember);
        double discount = subtotal - total;
        String transactionId = generateTransactionId();

        // Handle WALLET payment - deduct from member's credit balance
        if ("WALLET".equalsIgnoreCase(paymentMethod)) {
            if (!isMember) {
                throw new InvalidInputException("Wallet payment is only available for members");
            }
            if (memberRepository == null) {
                throw new InvalidInputException("Member repository not available");
            }
            
            MemberRecord member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("Member not found: " + request.getMemberId()));
            
            if (member.getCreditBalance() < total) {
                throw new InvalidInputException("Insufficient wallet balance");
            }
            
            // Deduct the amount from member's balance
            member.setCreditBalance(member.getCreditBalance() - total);
            memberRepository.update(member);
            LOG.info("Deducted RM{} from member {} wallet", total, request.getMemberId());
        }

        List<TransactionItem> itemsWithId = new ArrayList<>();
        for (TransactionItem item : transactionItems) {
            itemsWithId.add(new TransactionItem(
                    transactionId,
                    item.getLineNo(),
                    item.getProductId(),
                    item.getSize(),
                    item.getQuantity(),
                    item.getUnitPrice()
            ));
        }

        TransactionHeader header = new TransactionHeader(
                transactionId,
                LocalDateTime.now(),
                request.getMemberId(),
                request.getCustomerId(),
                total,
                paymentMethod
        );
        transactionRepository.saveTransaction(header, itemsWithId);

        for (OrderItemRequest itemRequest : request.getItems()) {
            inventoryService.reduceStock(itemRequest.getProductId(), itemRequest.getSize(), itemRequest.getQuantity());
        }

        LOG.info("Order {} placed with {} items (member={}, payment={})", transactionId, transactionItems.size(), isMember, paymentMethod);
        return new OrderResult(transactionId, subtotal, discount, total, isMember);
    }

    private String generateTransactionId() {
        int max = 0;
        for (TransactionHeader header : transactionRepository.findAllHeaders()) {
            String id = header.getTransactionId();
            if (id != null && id.startsWith("T")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignore) {
                    // skip malformed ids
                }
            }
        }
        return "T" + String.format("%04d", max + 1);
    }

    private ProductRecord getProductOrThrow(String productId) {
        Optional<ProductRecord> product = productRepository.findById(productId);
        return product.orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
    }
}
