package com.sareeaura.product.service;

import com.sareeaura.category.entity.Category;
import com.sareeaura.category.repository.CategoryRepository;
import com.sareeaura.exception.ResourceNotFoundException;
import com.sareeaura.inventory.entity.Inventory;
import com.sareeaura.inventory.repository.InventoryRepository;
import com.sareeaura.product.dto.ProductRequest;
import com.sareeaura.product.dto.ProductResponse;
import com.sareeaura.product.entity.Product;
import com.sareeaura.product.entity.ProductImage;
import com.sareeaura.product.repository.ProductImageRepository;
import com.sareeaura.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public Page<ProductResponse> getProductsWithFilters(
            String query,
            Long categoryId,
            String fabric,
            String color,
            String occasion,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sortBy,
            int page,
            int size
    ) {
        return getProductsWithFilters(query, categoryId, fabric, color, occasion, minPrice, maxPrice, null, null, sortBy, page, size);
    }

    public Page<ProductResponse> getProductsWithFilters(
            String query,
            Long categoryId,
            String fabric,
            String color,
            String occasion,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isNewArrival,
            String sortBy,
            int page,
            int size
    ) {
        return getProductsWithFilters(query, categoryId, fabric, color, occasion, minPrice, maxPrice, isNewArrival, null, sortBy, page, size);
    }

    public Page<ProductResponse> getProductsWithFilters(
            String query,
            Long categoryId,
            String fabric,
            String color,
            String occasion,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isNewArrival,
            Boolean onSale,
            String sortBy,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "sellingPrice");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "sellingPrice");
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "rating");
        } else if ("popular".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "reviewCount");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findWithFilters(
                query, categoryId, fabric, color, occasion, minPrice, maxPrice, isNewArrival, onSale, pageable
        );

        return productPage.map(this::mapToResponse);
    }

    public List<ProductResponse> getNewArrivals() {
        return productRepository.findByActiveTrueAndIsNewArrivalTrueOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getBestSellers() {
        return productRepository.findTop8ByActiveTrueAndIsBestSellerTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findTop8ByActiveTrueAndIsFeaturedTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        int discount = 0;
        if (request.getMrp().compareTo(BigDecimal.ZERO) > 0 && request.getSellingPrice().compareTo(request.getMrp()) < 0) {
            BigDecimal diff = request.getMrp().subtract(request.getSellingPrice());
            discount = diff.multiply(BigDecimal.valueOf(100)).divide(request.getMrp(), 0, RoundingMode.HALF_UP).intValue();
        }

        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .description(request.getDescription())
                .category(category)
                .fabric(request.getFabric())
                .color(request.getColor())
                .pattern(request.getPattern())
                .occasion(request.getOccasion())
                .sareeLength(request.getSareeLength() != null ? request.getSareeLength() : "5.5 Meters")
                .blouseDetails(request.getBlouseDetails() != null ? request.getBlouseDetails() : "Includes 0.8 Meter Unstitched Blouse Piece")
                .mrp(request.getMrp())
                .sellingPrice(request.getSellingPrice())
                .discountPercentage(discount)
                .stock(request.getStock())
                .isFeatured(request.isFeatured())
                .isBestSeller(request.isBestSeller())
                .isNewArrival(request.isNewArrival())
                .active(true)
                .build();

        product = productRepository.save(product);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                images.add(ProductImage.builder()
                        .product(product)
                        .imageUrl(request.getImageUrls().get(i))
                        .isPrimary(i == 0)
                        .displayOrder(i)
                        .build());
            }
            productImageRepository.saveAll(images);
            product.setImages(images);
        }

        inventoryRepository.save(Inventory.builder()
                .product(product)
                .availableQuantity(request.getStock())
                .reservedQuantity(0)
                .soldQuantity(0)
                .build());

        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setFabric(request.getFabric());
        product.setColor(request.getColor());
        product.setPattern(request.getPattern());
        product.setOccasion(request.getOccasion());
        product.setSareeLength(request.getSareeLength());
        product.setBlouseDetails(request.getBlouseDetails());
        product.setMrp(request.getMrp());
        product.setSellingPrice(request.getSellingPrice());

        int discount = 0;
        if (request.getMrp() != null && request.getSellingPrice() != null
                && request.getMrp().compareTo(BigDecimal.ZERO) > 0
                && request.getSellingPrice().compareTo(request.getMrp()) < 0) {
            BigDecimal diff = request.getMrp().subtract(request.getSellingPrice());
            discount = diff.multiply(BigDecimal.valueOf(100)).divide(request.getMrp(), 0, RoundingMode.HALF_UP).intValue();
        }
        product.setDiscountPercentage(discount);

        product.setFeatured(request.isFeatured());
        product.setBestSeller(request.isBestSeller());
        product.setNewArrival(request.isNewArrival());

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            if (product.getImages() == null) {
                product.setImages(new ArrayList<>());
            } else {
                product.getImages().clear();
            }
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                product.getImages().add(ProductImage.builder()
                        .product(product)
                        .imageUrl(request.getImageUrls().get(i))
                        .isPrimary(i == 0)
                        .displayOrder(i)
                        .build());
            }
        }

        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(product.getId());
        if (inventoryOpt.isPresent()) {
            Inventory inv = inventoryOpt.get();
            inv.setAvailableQuantity(request.getStock());
            inventoryRepository.save(inv);
        } else {
            inventoryRepository.save(Inventory.builder()
                    .product(product)
                    .availableQuantity(request.getStock())
                    .reservedQuantity(0)
                    .soldQuantity(0)
                    .build());
        }

        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setActive(false);
        productRepository.save(product);
    }

    public ProductResponse mapToResponse(Product p) {
        List<String> imageUrls = p.getImages() != null ?
                p.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .map(url -> url != null && url.contains("photo-1610030469830") ?
                                "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80" : url)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .sku(p.getSku())
                .description(p.getDescription())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .categorySlug(p.getCategory().getSlug())
                .fabric(p.getFabric())
                .color(p.getColor())
                .pattern(p.getPattern())
                .occasion(p.getOccasion())
                .sareeLength(p.getSareeLength())
                .blouseDetails(p.getBlouseDetails())
                .mrp(p.getMrp())
                .sellingPrice(p.getSellingPrice())
                .discountPercentage(p.getDiscountPercentage())
                .stock(p.getStock())
                .inStock(p.getStock() > 0)
                .isFeatured(p.isFeatured())
                .isBestSeller(p.isBestSeller())
                .isNewArrival(p.isNewArrival())
                .rating(p.getRating())
                .reviewCount(p.getReviewCount())
                .primaryImageUrl(p.getPrimaryImageUrl())
                .images(imageUrls)
                .build();
    }
}
