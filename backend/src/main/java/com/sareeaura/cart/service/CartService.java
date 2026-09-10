package com.sareeaura.cart.service;

import com.sareeaura.auth.service.AuthService;
import com.sareeaura.cart.dto.AddToCartRequest;
import com.sareeaura.cart.dto.CartItemResponse;
import com.sareeaura.cart.dto.CartResponse;
import com.sareeaura.cart.entity.Cart;
import com.sareeaura.cart.entity.CartItem;
import com.sareeaura.cart.repository.CartItemRepository;
import com.sareeaura.cart.repository.CartRepository;
import com.sareeaura.exception.BadRequestException;
import com.sareeaura.exception.ResourceNotFoundException;
import com.sareeaura.product.entity.Product;
import com.sareeaura.product.repository.ProductRepository;
import com.sareeaura.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    @Transactional(readOnly = true)
    public CartResponse getCart() {
        User user = authService.getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        User user = authService.getCurrentUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (!product.isActive()) {
            throw new BadRequestException("Product is currently unavailable");
        }

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock (" + product.getStock() + " available)");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStock()) {
                throw new BadRequestException("Cannot add more. Exceeds available stock (" + product.getStock() + " available)");
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse updateQuantity(Long itemId, int quantity) {
        if (quantity < 1) {
            return removeFromCart(itemId);
        }

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (quantity > item.getProduct().getStock()) {
            throw new BadRequestException("Requested quantity exceeds available stock");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return mapToResponse(item.getCart());
    }

    @Transactional
    public CartResponse removeFromCart(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));
        Cart cart = item.getCart();
        cart.removeItem(item);
        cartItemRepository.delete(item);
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse clearCart() {
        User user = authService.getCurrentUser();
        Cart cart = getOrCreateCart(user);
        cart.clearItems();
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    public CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItems = 0;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                Product p = item.getProduct();
                BigDecimal itemTotal = p.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemTotal);
                totalItems += item.getQuantity();

                items.add(CartItemResponse.builder()
                        .id(item.getId())
                        .productId(p.getId())
                        .productName(p.getName())
                        .productSku(p.getSku())
                        .imageUrl(p.getPrimaryImageUrl())
                        .mrp(p.getMrp())
                        .sellingPrice(p.getSellingPrice())
                        .quantity(item.getQuantity())
                        .stock(p.getStock())
                        .subtotal(itemTotal)
                        .build());
            }
        }

        // Complimentary shipping over ₹5000, else ₹150
        BigDecimal shipping = (subtotal.compareTo(BigDecimal.valueOf(5000)) >= 0 || totalItems == 0) ?
                BigDecimal.ZERO : BigDecimal.valueOf(150);

        BigDecimal grandTotal = subtotal.add(shipping);

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .discount(BigDecimal.ZERO)
                .shippingCharge(shipping)
                .grandTotal(grandTotal)
                .build();
    }
}
