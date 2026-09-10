package com.sareeaura.common.seeder;

import com.sareeaura.banner.entity.Banner;
import com.sareeaura.banner.repository.BannerRepository;
import com.sareeaura.cart.entity.Cart;
import com.sareeaura.cart.repository.CartRepository;
import com.sareeaura.category.entity.Category;
import com.sareeaura.category.repository.CategoryRepository;
import com.sareeaura.coupon.entity.Coupon;
import com.sareeaura.coupon.entity.DiscountType;
import com.sareeaura.coupon.repository.CouponRepository;
import com.sareeaura.inventory.entity.Inventory;
import com.sareeaura.inventory.repository.InventoryRepository;
import com.sareeaura.product.entity.Product;
import com.sareeaura.product.entity.ProductImage;
import com.sareeaura.product.repository.ProductImageRepository;
import com.sareeaura.product.repository.ProductRepository;
import com.sareeaura.user.entity.*;
import com.sareeaura.user.repository.AddressRepository;
import com.sareeaura.user.repository.RoleRepository;
import com.sareeaura.user.repository.UserRepository;
import com.sareeaura.wishlist.entity.Wishlist;
import com.sareeaura.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final InventoryRepository inventoryRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final CouponRepository couponRepository;
    private final BannerRepository bannerRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing SareeAura production seed data...");

        try {
            jdbcTemplate.update("UPDATE product_images SET image_url = 'https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80' WHERE image_url LIKE '%photo-1610030469830%'");
            jdbcTemplate.update("UPDATE categories SET image_url = 'https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80' WHERE image_url LIKE '%photo-1610030469830%'");
            jdbcTemplate.update("UPDATE product_images SET image_url = 'http://localhost:8080/uploads/teal_ethnic_suit_embroidered.jpg' WHERE image_url LIKE '%1605369572399%'");
            jdbcTemplate.update("UPDATE categories SET image_url = 'http://localhost:8080/uploads/teal_ethnic_suit_embroidered.jpg' WHERE image_url LIKE '%1605369572399%'");
            jdbcTemplate.update("UPDATE banners SET image_url = 'http://localhost:8080/uploads/teal_ethnic_suit_embroidered.jpg' WHERE image_url LIKE '%1605369572399%'");
        } catch (Exception e) {
            log.warn("Image update note: {}", e.getMessage());
        }

        // 1. Roles
        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).build()));
        Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_CUSTOMER).build()));

        // 2. Default Admin
        if (!userRepository.existsByEmail("admin@sareeaura.com")) {
            User admin = User.builder()
                    .email("admin@sareeaura.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .firstName("Master")
                    .lastName("Administrator")
                    .phone("+91 9876543210")
                    .active(true)
                    .roles(Set.of(adminRole, customerRole))
                    .build();
            admin = userRepository.save(admin);
            cartRepository.save(Cart.builder().user(admin).build());
            wishlistRepository.save(Wishlist.builder().user(admin).build());
            log.info("Seeded Admin: admin@sareeaura.com / Admin@123");
        }

        // 3. Default Customer
        User customer = null;
        if (!userRepository.existsByEmail("customer@sareeaura.com")) {
            customer = User.builder()
                    .email("customer@sareeaura.com")
                    .password(passwordEncoder.encode("Customer@123"))
                    .firstName("Ananya")
                    .lastName("Sharma")
                    .phone("+91 9876501234")
                    .active(true)
                    .roles(Set.of(customerRole))
                    .build();
            customer = userRepository.save(customer);
            cartRepository.save(Cart.builder().user(customer).build());
            wishlistRepository.save(Wishlist.builder().user(customer).build());

            addressRepository.save(Address.builder()
                    .user(customer)
                    .fullName("Ananya Sharma")
                    .phone("+91 9876501234")
                    .addressLine1("B-402, Royal Palms Residency, Indiranagar")
                    .addressLine2("Near 100 Feet Road")
                    .city("Bengaluru")
                    .state("Karnataka")
                    .postalCode("560038")
                    .country("India")
                    .addressType(AddressType.HOME)
                    .isDefault(true)
                    .build());
            log.info("Seeded Customer: customer@sareeaura.com / Customer@123");
        }

        // 4. Categories
        Category kanchi = getOrCreateCategory("Kanchipuram Silk", "kanchipuram-silk",
                "Authentic pure mulberry silk woven with pure silver and gold zari threads.",
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80");

        Category banarasi = getOrCreateCategory("Banarasi Brocade", "banarasi-silk",
                "Opulent silk sarees from Varanasi embellished with intricate floral jal and royal borders.",
                "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80");

        Category bridal = getOrCreateCategory("Bridal & Wedding Edit", "bridal-sarees",
                "Curated bridal trousseau masterpieces crafted for the most auspicious moments.",
                "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80");

        Category organza = getOrCreateCategory("Chanderi & Organza", "chanderi-organza",
                "Featherlight sheer silks and gossamer organzas detailed with delicate hand embroidery.",
                "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80");

        Category tussar = getOrCreateCategory("Tussar & Raw Silk", "tussar-silk",
                "Textured hand-reeled wild silk sarees celebrated for their rich earthy sheen.",
                "https://images.unsplash.com/photo-1609357605129-26f69add5d6e?auto=format&fit=crop&w=800&q=80");

        Category cotton = getOrCreateCategory("Cotton & Linen Weaves", "cotton-linen",
                "Breathable fine count handloom cottons and artisanal linen sarees for effortless grace.",
                "http://localhost:8080/uploads/teal_ethnic_suit_embroidered.jpg");

        // 5. Products (if count < 20)
        if (productRepository.count() < 20) {
            log.info("Seeding 20+ luxury Sarees...");
            seedSarees(kanchi, banarasi, bridal, organza, tussar, cotton);
        }

        // 6. Coupons
        if (couponRepository.count() == 0) {
            couponRepository.save(Coupon.builder()
                    .code("WELCOME10")
                    .description("10% instant discount on your inaugural luxury handloom order")
                    .discountType(DiscountType.PERCENTAGE)
                    .discountValue(BigDecimal.valueOf(10))
                    .minOrderAmount(BigDecimal.valueOf(2000))
                    .maxDiscountAmount(BigDecimal.valueOf(1500))
                    .startDate(LocalDateTime.now().minusDays(1))
                    .expiryDate(LocalDateTime.now().plusMonths(6))
                    .active(true)
                    .build());

            couponRepository.save(Coupon.builder()
                    .code("FESTIVE20")
                    .description("Flat 20% festive discount on grand bridal and festive silks")
                    .discountType(DiscountType.PERCENTAGE)
                    .discountValue(BigDecimal.valueOf(20))
                    .minOrderAmount(BigDecimal.valueOf(6000))
                    .maxDiscountAmount(BigDecimal.valueOf(3000))
                    .startDate(LocalDateTime.now().minusDays(1))
                    .expiryDate(LocalDateTime.now().plusMonths(3))
                    .active(true)
                    .build());

            couponRepository.save(Coupon.builder()
                    .code("AURA500")
                    .description("Flat ₹500 discount on orders above ₹3,500")
                    .discountType(DiscountType.FIXED)
                    .discountValue(BigDecimal.valueOf(500))
                    .minOrderAmount(BigDecimal.valueOf(3500))
                    .startDate(LocalDateTime.now().minusDays(1))
                    .expiryDate(LocalDateTime.now().plusMonths(6))
                    .active(true)
                    .build());
            log.info("Seeded promotional coupons: WELCOME10, FESTIVE20, AURA500");
        }

        // 7. Banners
        if (bannerRepository.count() == 0) {
            bannerRepository.save(Banner.builder()
                    .title("Royal Kanchipuram Silks")
                    .subtitle("Woven with pure gold and silver zari by master weavers of Tamil Nadu")
                    .imageUrl("https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=1600&q=80")
                    .ctaText("Explore Royal Collection")
                    .targetUrl("/shop?category=kanchipuram")
                    .displayOrder(1)
                    .active(true)
                    .build());

            bannerRepository.save(Banner.builder()
                    .title("Banarasi Heritage Weaves")
                    .subtitle("Timeless brocades, floral vines, and regal elegance for celebrations")
                    .imageUrl("https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=1600&q=80")
                    .ctaText("Shop Banarasi Edit")
                    .targetUrl("/shop?category=banarasi")
                    .displayOrder(2)
                    .active(true)
                    .build());
            log.info("Seeded homepage hero banners");
        }

        log.info("SareeAura seed data initialized successfully!");
    }

    private Category getOrCreateCategory(String name, String slug, String desc, String img) {
        return categoryRepository.findBySlug(slug)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(name)
                        .slug(slug)
                        .description(desc)
                        .imageUrl(img)
                        .active(true)
                        .build()));
    }

    private void seedSarees(Category kanchi, Category banarasi, Category bridal, Category organza, Category tussar, Category cotton) {
        createSaree(kanchi, "Maharani Crimson Gold Kanchipuram Silk Saree", "SA-KAN-001",
                "A regal crimson pure silk saree handwoven with opulent gold zari peacock motifs, grand pallu, and korvai borders.",
                "Pure Mulberry Silk", "Crimson Red", "Temple Borders & Peacocks", "Wedding / Bridal",
                new BigDecimal("18500.00"), new BigDecimal("14800.00"), 20, 12, true, true, false,
                List.of(
                        "https://images.unsplash.com/photo-1599587425414-b15c92c84799?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1599587425394-4b5c777e5d83?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(banarasi, "Royal Shahi Banarasi Katan Silk Brocade Saree", "SA-BAN-002",
                "Masterfully crafted in Varanasi using pure Katan silk with intricate floral kadwa motifs and lustrous gold brocade.",
                "Pure Katan Silk", "Emerald Green", "Floral Jaal & Kadwa", "Festive / Reception",
                new BigDecimal("21000.00"), new BigDecimal("16800.00"), 20, 15, true, true, false,
                List.of(
                        "https://images.unsplash.com/photo-1588647895254-8e3d0d828230?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(bridal, "Imperial Scarlet Wedding Trousseau Silk Saree", "SA-BRI-003",
                "An heirloom bridal masterpiece featuring heavy zari buttis, temple borders, and an elaborately woven royal pallu.",
                "Pure Kanchipuram Silk", "Deep Scarlet", "Mayil & Rudraksha", "Bridal",
                new BigDecimal("32000.00"), new BigDecimal("25600.00"), 20, 8, true, true, true,
                List.of(
                        "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(organza, "Pastel Blush Embroidered Sheer Organza Saree", "SA-ORG-004",
                "Delicate organza saree in blush pink adorned with hand-cut scalloped borders and glistening pearl sequin highlights.",
                "Pure Silk Organza", "Blush Pink", "Scallop Floral Embroidery", "Cocktail / Party",
                new BigDecimal("9500.00"), new BigDecimal("7600.00"), 20, 14, false, false, true,
                List.of(
                        "https://images.unsplash.com/photo-1608465682855-6b3a04291fd0?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(tussar, "Artisanal Charcoal Hand-Painted Tussar Silk Saree", "SA-TUS-005",
                "Textured wild Tussar silk in deep charcoal hand-painted by master artisans with authentic Madhubani art.",
                "Handloom Tussar", "Charcoal Grey", "Hand-Painted Madhubani", "Artisanal Connoisseur",
                new BigDecimal("12000.00"), new BigDecimal("9600.00"), 20, 10, true, false, true,
                List.of(
                        "https://images.unsplash.com/photo-1606787366850-de6330128bfc?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(cotton, "Indigo Ajrakh Block Printed Chanderi Cotton Saree", "SA-COT-006",
                "Fine count cotton-silk blend hand-blocked with natural vegetable dyes and highlighted with subtle zari patti.",
                "Chanderi Cotton-Silk", "Royal Indigo", "Geometric Ajrakh", "Work / Daily Festive",
                new BigDecimal("4500.00"), new BigDecimal("3600.00"), 20, 25, false, false, true,
                List.of(
                        "https://images.unsplash.com/photo-1598928636135-d146006fffde?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(kanchi, "Aura Sunset Mustard & Magenta Kanchipuram Silk Saree", "SA-KAN-007",
                "A breathtaking mustard yellow silk saree with contrasting royal magenta pallu and pure gold zari borders.",
                "Pure Mulberry Silk", "Mustard Yellow", "Ganga Jamuna Borders", "Pooja / Engagement",
                new BigDecimal("16500.00"), new BigDecimal("13200.00"), 20, 10, true, true, true,
                List.of(
                        "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(banarasi, "Midnight Blue Meenakari Banarasi Silk Saree", "SA-BAN-008",
                "Stately midnight blue Katan silk saree highlighted with vibrant multi-colored Meenakari floral craftsmanship.",
                "Pure Katan Silk", "Midnight Blue", "Meenakari Floral Jaal", "Reception / Gala",
                new BigDecimal("24000.00"), new BigDecimal("19200.00"), 20, 7, true, true, false,
                List.of(
                        "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(bridal, "Rani Pink Royal Bridal Silk Saree", "SA-BRI-009",
                "Traditional Rani pink bridal saree with heavy zari pallu, coin buttis, and intricate floral border weaving.",
                "Pure Kanchipuram Silk", "Rani Pink", "Coin Butti & Mayil", "Wedding Ceremony",
                new BigDecimal("29000.00"), new BigDecimal("23200.00"), 20, 6, true, true, false,
                List.of(
                        "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(organza, "Mint Sage Floral Organza Silk Saree", "SA-ORG-010",
                "Graceful mint green translucent organza saree detailed with metallic thread borders and floral prints.",
                "Silk Organza", "Mint Sage", "Floral Prints & Zari Border", "Day Wedding / Sangeet",
                new BigDecimal("8200.00"), new BigDecimal("6500.00"), 20, 18, false, false, true,
                List.of(
                        "https://images.unsplash.com/photo-1610030469830-580a5814be95?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(cotton, "Handwoven Jamdani Muslin Cotton Saree", "SA-COT-011",
                "Featherlight Bengal muslin cotton woven with legendary Jamdani geometric motifs and airy drape.",
                "Fine Muslin Cotton", "Ivory White", "Geometric Jamdani", "Summer Celebrations",
                new BigDecimal("6800.00"), new BigDecimal("5400.00"), 20, 20, false, true, false,
                List.of(
                        "http://localhost:8080/uploads/teal_ethnic_suit_embroidered.jpg"
                ));

        createSaree(kanchi, "Peacock Teal Dual-Tone Kanchipuram Silk Saree", "SA-KAN-012",
                "Dazzling shot silk weave shimmering between peacock green and deep blue with antique gold zari border.",
                "Pure Mulberry Silk", "Peacock Teal", "Antique Zari Waves", "Festive",
                new BigDecimal("19500.00"), new BigDecimal("15600.00"), 20, 11, true, false, true,
                List.of(
                        "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(banarasi, "Ruby Red Vintage Banarasi Tanchoi Silk Saree", "SA-BAN-013",
                "Extraordinarily soft Tanchoi silk saree with self-weave satin textures and regal gilded borders.",
                "Tanchoi Pure Silk", "Ruby Red", "Satin Paisley Weave", "Cocktail / Dinner",
                new BigDecimal("14500.00"), new BigDecimal("11600.00"), 20, 13, false, true, true,
                List.of(
                        "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(tussar, "Gheo Tussar Silk Saree with Kantha Stitch Work", "SA-TUS-014",
                "Organic raw tussar silk enriched with exquisite artisan hand Kantha stitch threadwork along the border.",
                "Handloom Tussar", "Beige & Multicolored", "Kantha Thread Work", "Artistic Gathering",
                new BigDecimal("11200.00"), new BigDecimal("8900.00"), 20, 9, false, false, true,
                List.of(
                        "https://images.unsplash.com/photo-1609357605129-26f69add5d6e?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(cotton, "Chanderi Gold Tissue Silk Saree", "SA-COT-015",
                "Luminous tissue saree woven with golden warp and fine cotton weft for a lightweight celestial shine.",
                "Tissue Chanderi Silk", "Champagne Gold", "Fine Ribbed Tissue", "Festive Evening",
                new BigDecimal("7800.00"), new BigDecimal("6200.00"), 20, 16, true, false, false,
                List.of(
                        "http://localhost:8080/uploads/teal_ethnic_suit_embroidered.jpg"
                ));

        createSaree(kanchi, "Traditional Korvai Purple Kanchipuram Silk Saree", "SA-KAN-016",
                "Authentic interlocking Korvai technique with deep violet body and fiery orange gold zari border.",
                "Pure Mulberry Silk", "Deep Violet", "Korvai Temple Border", "Traditional Weddings",
                new BigDecimal("22500.00"), new BigDecimal("18000.00"), 20, 8, true, true, false,
                List.of(
                        "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(bridal, "Golden Ochre Heritage Bridal Brocade Saree", "SA-BRI-017",
                "A showstopping golden ochre bridal drape fully embellished with pure zari Shikargah hunting motifs.",
                "Heavy Katan Silk", "Golden Ochre", "Shikargah Motifs", "Grand Wedding",
                new BigDecimal("36000.00"), new BigDecimal("28800.00"), 20, 5, true, true, false,
                List.of(
                        "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(organza, "Lavender Lilac Zardozi Sheer Organza Saree", "SA-ORG-018",
                "Ethereal lilac organza draped with subtle silver zardozi embroidery and light pearl borders.",
                "Pure Organza", "Lilac Lavender", "Zardozi & Pearl Patti", "Cocktail Evening",
                new BigDecimal("10500.00"), new BigDecimal("8400.00"), 20, 12, false, true, true,
                List.of(
                        "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(tussar, "Forest Green Ghicha Tussar Silk Saree", "SA-TUS-019",
                "Substantial textured Ghicha silk in deep forest green with bronze zari stripes across the pallu.",
                "Ghicha Tussar Silk", "Forest Green", "Bronze Zari Striped Pallu", "Formal Occasion",
                new BigDecimal("13500.00"), new BigDecimal("10800.00"), 20, 14, false, false, true,
                List.of(
                        "https://images.unsplash.com/photo-1609357605129-26f69add5d6e?auto=format&fit=crop&w=800&q=80"
                ));

        createSaree(cotton, "Kalamkari Hand-Painted Cotton Saree", "SA-COT-020",
                "Eco-friendly organic cotton saree featuring handcrafted Srikalahasti mythological tree of life paintings.",
                "Pure Organic Cotton", "Earthy Ochre & Rust", "Kalamkari Tree of Life", "Casual / Festive",
                new BigDecimal("5200.00"), new BigDecimal("4160.00"), 20, 22, false, true, false,
                List.of(
                        "http://localhost:8080/uploads/teal_ethnic_suit_embroidered.jpg"
                ));
    }

    private void createSaree(
            Category category, String name, String sku, String desc,
            String fabric, String color, String pattern, String occasion,
            BigDecimal mrp, BigDecimal sellingPrice, int discount, int stock,
            boolean isFeatured, boolean isBestSeller, boolean isNewArrival,
            List<String> imageUrls
    ) {
        java.util.Optional<Product> existingOpt = productRepository.findBySku(sku);
        if (existingOpt.isPresent()) {
            Product existing = existingOpt.get();
            if (existing.getImages() != null) {
                for (ProductImage img : existing.getImages()) {
                    if (img.getImageUrl() != null && img.getImageUrl().contains("photo-1610030469830")) {
                        img.setImageUrl(imageUrls.get(0));
                        productImageRepository.save(img);
                    }
                }
            }
            return;
        }

        Product product = Product.builder()
                .name(name)
                .sku(sku)
                .description(desc)
                .category(category)
                .fabric(fabric)
                .color(color)
                .pattern(pattern)
                .occasion(occasion)
                .sareeLength("5.5 Meters")
                .blouseDetails("Includes 0.8 Meter Unstitched Matching Blouse Piece")
                .mrp(mrp)
                .sellingPrice(sellingPrice)
                .discountPercentage(discount)
                .stock(stock)
                .isFeatured(isFeatured)
                .isBestSeller(isBestSeller)
                .isNewArrival(isNewArrival)
                .rating(BigDecimal.valueOf(4.8))
                .reviewCount(14)
                .active(true)
                .build();

        product = productRepository.save(product);

        List<ProductImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            images.add(ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrls.get(i))
                    .isPrimary(i == 0)
                    .displayOrder(i)
                    .build());
        }
        productImageRepository.saveAll(images);
        product.setImages(images);

        inventoryRepository.save(Inventory.builder()
                .product(product)
                .availableQuantity(stock)
                .reservedQuantity(0)
                .soldQuantity(5)
                .build());
    }
}
