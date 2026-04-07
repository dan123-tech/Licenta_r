package com.onlinerental.service;

import com.onlinerental.domain.InventoryStatus;
import com.onlinerental.domain.InventoryUnit;
import com.onlinerental.domain.Product;
import com.onlinerental.domain.User;
import com.onlinerental.repository.InventoryUnitRepository;
import com.onlinerental.repository.ProductRepository;
import com.onlinerental.repository.ProductReviewRepository;
import com.onlinerental.repository.UserRepository;
import com.onlinerental.web.DtoMapper;
import com.onlinerental.web.dto.InventoryDto;
import com.onlinerental.web.dto.InventoryRequest;
import com.onlinerental.web.dto.ProductDto;
import com.onlinerental.web.dto.ProductRequest;
import com.onlinerental.web.dto.ProductReviewDto;
import com.onlinerental.web.dto.ProductReviewSummaryDto;
import com.onlinerental.web.dto.ReviewUpsertRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryUnitRepository inventoryUnitRepository;
    private final ProductReviewRepository productReviewRepository;
    private final UserRepository userRepository;

    public List<ProductDto> listFiltered(String category, String brand, String model) {
        String c = emptyToNull(category);
        String b = emptyToNull(brand);
        String m = emptyToNull(model);
        return productRepository.findWithFilters(c, b, m).stream().map(DtoMapper::toProductDto).toList();
    }

    public List<String> categories() {
        return productRepository.findDistinctCategories();
    }

    public List<String> brands(String category) {
        return productRepository.findDistinctBrands(emptyToNull(category));
    }

    public List<String> models(String category, String brand) {
        return productRepository.findDistinctModels(emptyToNull(category), emptyToNull(brand));
    }

    public ProductDto getById(Long id) {
        return DtoMapper.toProductDto(productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produs inexistent")));
    }

    @Transactional
    public ProductDto create(ProductRequest req, String ownerUsername) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Utilizator inexistent"));
        Product p = Product.builder()
                .owner(owner)
                .name(req.name())
                .description(req.description())
                .dailyPrice(req.dailyPrice())
                .category(req.category())
                .brand(req.brand())
                .model(req.model())
                .imageUrl(req.imageUrl())
                .discountPercent(req.discountPercent() != null ? req.discountPercent() : BigDecimal.ZERO)
                .build();
        return DtoMapper.toProductDto(productRepository.save(p));
    }

    @Transactional
    public ProductDto update(Long id, ProductRequest req, User actor) {
        Product p = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Produs inexistent"));
        assertCanManageProduct(actor, p);
        p.setName(req.name());
        p.setDescription(req.description());
        p.setDailyPrice(req.dailyPrice());
        p.setCategory(req.category());
        p.setBrand(req.brand());
        p.setModel(req.model());
        p.setImageUrl(req.imageUrl());
        p.setDiscountPercent(req.discountPercent() != null ? req.discountPercent() : BigDecimal.ZERO);
        p.setUpdatedAt(Instant.now());
        return DtoMapper.toProductDto(productRepository.save(p));
    }

    @Transactional
    public void delete(Long id, User actor) {
        Product p = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Produs inexistent"));
        assertCanManageProduct(actor, p);
        productRepository.delete(p);
    }

    @Transactional
    public InventoryDto addInventory(InventoryRequest req, User actor) {
        Product p = productRepository.findById(req.productId())
                .orElseThrow(() -> new IllegalArgumentException("Produs inexistent"));
        assertCanManageProduct(actor, p);
        if (inventoryUnitRepository.existsByProductIdAndSerialNumber(req.productId(), req.serialNumber())) {
            throw new IllegalArgumentException("Serie deja înregistrată pentru acest produs");
        }
        InventoryUnit unit = InventoryUnit.builder()
                .product(p)
                .serialNumber(req.serialNumber())
                .status(InventoryStatus.AVAILABLE)
                .build();
        return DtoMapper.toInventoryDto(inventoryUnitRepository.save(unit), true);
    }

    public List<InventoryDto> availableInventory(Long productId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produs inexistent"));
        return inventoryUnitRepository.findByProductAndStatus(p, InventoryStatus.AVAILABLE).stream()
                .map(u -> DtoMapper.toInventoryDto(u, false))
                .toList();
    }

    public List<ProductDto> myProducts(String username) {
        User u = userRepository.findByUsername(username).orElseThrow();
        return productRepository.findByOwner(u).stream().map(DtoMapper::toProductDto).toList();
    }

    public List<ProductReviewDto> reviews(Long productId) {
        Product p = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Produs inexistent"));
        return productReviewRepository.findByProductOrderByCreatedAtDesc(p).stream()
                .map(r -> new ProductReviewDto(
                        r.getId(),
                        p.getId(),
                        r.getUser().getId(),
                        r.getUser().getUsername(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ))
                .toList();
    }

    public ProductReviewSummaryDto reviewSummary(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Produs inexistent");
        }
        Double avg = productReviewRepository.averageRatingByProductId(productId);
        long count = productReviewRepository.countByProductId(productId);
        return new ProductReviewSummaryDto(productId, avg != null ? avg : 0.0, count);
    }

    @Transactional
    public ProductReviewDto upsertReview(Long productId, ReviewUpsertRequest req, String username) {
        Product p = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Produs inexistent"));
        User u = userRepository.findByUsername(username).orElseThrow();
        var existing = productReviewRepository.findByProductAndUser(p, u);
        var review = existing.orElseGet(() -> com.onlinerental.domain.ProductReview.builder()
                .product(p)
                .user(u)
                .build());
        review.setRating(req.rating());
        review.setComment(req.comment());
        review.setUpdatedAt(Instant.now());
        var saved = productReviewRepository.save(review);
        return new ProductReviewDto(
                saved.getId(),
                p.getId(),
                u.getId(),
                u.getUsername(),
                saved.getRating(),
                saved.getComment(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    public void assertCanManageProduct(User actor, Product p) {
        boolean superOrAdmin =
                actor.getRoles().contains("ROLE_ADMIN") || actor.getRoles().contains("ROLE_SUPEROWNER");
        boolean vendorOwner =
                actor.getRoles().contains("ROLE_VENDOR") && p.getOwner() != null
                        && p.getOwner().getId().equals(actor.getId());
        if (!superOrAdmin && !vendorOwner) {
            throw new IllegalStateException("Nu poți modifica acest produs.");
        }
    }

    public User requireUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Utilizator inexistent"));
    }

    private static String emptyToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}
