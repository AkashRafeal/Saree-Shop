package com.sareeaura.order.service;

import com.sareeaura.auth.service.AuthService;
import com.sareeaura.cart.entity.Cart;
import com.sareeaura.cart.entity.CartItem;
import com.sareeaura.cart.repository.CartRepository;
import com.sareeaura.coupon.dto.CouponResponse;
import com.sareeaura.coupon.dto.ValidateCouponRequest;
import com.sareeaura.coupon.service.CouponService;
import com.sareeaura.exception.BadRequestException;
import com.sareeaura.exception.ResourceNotFoundException;
import com.sareeaura.order.dto.CreateOrderRequest;
import com.sareeaura.order.dto.OrderItemResponse;
import com.sareeaura.order.dto.OrderResponse;
import com.sareeaura.order.entity.Order;
import com.sareeaura.order.entity.OrderItem;
import com.sareeaura.order.entity.OrderStatus;
import com.sareeaura.order.repository.OrderRepository;
import com.sareeaura.payment.entity.Payment;
import com.sareeaura.payment.entity.PaymentMethod;
import com.sareeaura.payment.entity.PaymentStatus;
import com.sareeaura.payment.repository.PaymentRepository;
import com.sareeaura.product.entity.Product;
import com.sareeaura.product.repository.ProductRepository;
import com.sareeaura.user.dto.AddressResponse;
import com.sareeaura.user.entity.Address;
import com.sareeaura.user.entity.User;
import com.sareeaura.user.repository.AddressRepository;
import com.sareeaura.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final CouponService couponService;
    private final AuthService authService;
    private final AddressService addressService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = authService.getCurrentUser();

        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), user.getId())
                .orElseThrow(() -> new BadRequestException("Selected shipping address is invalid"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("No active shopping cart found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Shopping cart is empty");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // Recheck stock & calculate subtotal strictly from DB prices
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new BadRequestException("Product no longer exists"));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Product '" + product.getName() + "' has insufficient stock (" + product.getStock() + " left)");
            }

            BigDecimal lineTotal = product.getSellingPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            // Deduct stock transactionally
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // Immutable historical snapshot
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .imageUrl(product.getPrimaryImageUrl())
                    .price(product.getSellingPrice())
                    .quantity(cartItem.getQuantity())
                    .discount(BigDecimal.ZERO)
                    .total(lineTotal)
                    .build();

            orderItems.add(orderItem);
        }

        // Coupon validation
        BigDecimal discount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            try {
                ValidateCouponRequest couponReq = new ValidateCouponRequest();
                couponReq.setCode(request.getCouponCode());
                couponReq.setOrderAmount(subtotal);
                CouponResponse couponRes = couponService.validateCoupon(couponReq);
                discount = couponRes.getCalculatedDiscount();
            } catch (Exception e) {
                // Ignore or proceed without discount
            }
        }

        // Shipping charge calculation
        BigDecimal shipping = subtotal.compareTo(BigDecimal.valueOf(5000)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(150);
        BigDecimal totalAmount = subtotal.subtract(discount).add(shipping);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) totalAmount = BigDecimal.ZERO;

        String orderNumber = "SA-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .shippingAddress(address)
                .status(OrderStatus.CONFIRMED)
                .subtotal(subtotal)
                .discount(discount)
                .shippingCharge(shipping)
                .totalAmount(totalAmount)
                .couponCode(request.getCouponCode())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "RAZORPAY")
                .paymentStatus("SUCCESS")
                .trackingNumber("IND" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .courierName("Blue Dart Express")
                .build();

        for (OrderItem item : orderItems) {
            order.addItem(item);
        }

        order = orderRepository.save(order);

        // Record payment
        Payment payment = Payment.builder()
                .order(order)
                .razorpayOrderId("order_" + UUID.randomUUID().toString().substring(0, 12))
                .razorpayPaymentId("pay_" + UUID.randomUUID().toString().substring(0, 12))
                .razorpaySignature("sig_" + UUID.randomUUID().toString().substring(0, 16))
                .amount(totalAmount)
                .currency("INR")
                .paymentMethod(PaymentMethod.RAZORPAY)
                .status(PaymentStatus.SUCCESS)
                .build();
        paymentRepository.save(payment);

        // Clear user cart
        cart.clearItems();
        cartRepository.save(cart);

        return mapToResponse(order);
    }

    public List<OrderResponse> getCustomerOrders() {
        User user = authService.getCurrentUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        User user = authService.getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        User user = authService.getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel order once it has been shipped or delivered");
        }

        order.setStatus(OrderStatus.CANCELLED);

        // Restore inventory
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product p = item.getProduct();
                p.setStock(p.getStock() + item.getQuantity());
                productRepository.save(p);
            }
        }

        return mapToResponse(orderRepository.save(order));
    }

    // Admin endpoints
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::mapToResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setStatus(status);
        return mapToResponse(orderRepository.save(order));
    }

    public OrderResponse mapToResponse(Order o) {
        List<OrderItemResponse> itemResponses = o.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct() != null ? i.getProduct().getId() : null)
                        .productName(i.getProductName())
                        .productSku(i.getProductSku())
                        .imageUrl(i.getImageUrl())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .discount(i.getDiscount())
                        .total(i.getTotal())
                        .build())
                .collect(Collectors.toList());

        AddressResponse addressResponse = addressService.mapToResponse(o.getShippingAddress());

        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .status(o.getStatus())
                .subtotal(o.getSubtotal())
                .discount(o.getDiscount())
                .shippingCharge(o.getShippingCharge())
                .totalAmount(o.getTotalAmount())
                .couponCode(o.getCouponCode())
                .paymentMethod(o.getPaymentMethod())
                .paymentStatus(o.getPaymentStatus())
                .trackingNumber(o.getTrackingNumber())
                .courierName(o.getCourierName())
                .shippingAddress(addressResponse)
                .items(itemResponses)
                .totalItems(itemResponses.stream().mapToInt(OrderItemResponse::getQuantity).sum())
                .createdAt(o.getCreatedAt())
                .build();
    }
}
