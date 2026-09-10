package com.sareeaura.wishlist.service;

import com.sareeaura.auth.service.AuthService;
import com.sareeaura.cart.dto.AddToCartRequest;
import com.sareeaura.cart.service.CartService;
import com.sareeaura.exception.ResourceNotFoundException;
import com.sareeaura.product.dto.ProductResponse;
import com.sareeaura.product.entity.Product;
import com.sareeaura.product.repository.ProductRepository;
import com.sareeaura.product.service.ProductService;
import com.sareeaura.user.entity.User;
import com.sareeaura.wishlist.dto.WishlistResponse;
import com.sareeaura.wishlist.entity.Wishlist;
import com.sareeaura.wishlist.entity.WishlistItem;
import com.sareeaura.wishlist.repository.WishlistItemRepository;
import com.sareeaura.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final AuthService authService;

    @Transactional
    public Wishlist getOrCreateWishlist(User user) {
        return wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> wishlistRepository.save(Wishlist.builder().user(user).build()));
    }

    @Transactional(readOnly = true)
    public WishlistResponse getWishlist() {
        User user = authService.getCurrentUser();
        Wishlist wishlist = getOrCreateWishlist(user);
        return mapToResponse(wishlist);
    }

    @Transactional
    public WishlistResponse toggleWishlist(Long productId) {
        User user = authService.getCurrentUser();
        Wishlist wishlist = getOrCreateWishlist(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Optional<WishlistItem> existing = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), product.getId());
        if (existing.isPresent()) {
            WishlistItem item = existing.get();
            wishlist.removeItem(item);
            wishlistItemRepository.delete(item);
        } else {
            WishlistItem item = WishlistItem.builder()
                    .wishlist(wishlist)
                    .product(product)
                    .build();
            wishlist.addItem(item);
            wishlistItemRepository.save(item);
        }

        return mapToResponse(wishlist);
    }

    @Transactional
    public WishlistResponse removeFromWishlist(Long productId) {
        User user = authService.getCurrentUser();
        Wishlist wishlist = getOrCreateWishlist(user);

        wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId)
                .ifPresent(item -> {
                    wishlist.removeItem(item);
                    wishlistItemRepository.delete(item);
                });

        return mapToResponse(wishlist);
    }

    @Transactional
    public void moveToCart(Long productId) {
        removeFromWishlist(productId);
        AddToCartRequest req = new AddToCartRequest();
        req.setProductId(productId);
        req.setQuantity(1);
        cartService.addToCart(req);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        List<ProductResponse> products = wishlist.getItems().stream()
                .map(item -> productService.mapToResponse(item.getProduct()))
                .collect(Collectors.toList());

        return WishlistResponse.builder()
                .id(wishlist.getId())
                .products(products)
                .totalItems(products.size())
                .build();
    }
}
