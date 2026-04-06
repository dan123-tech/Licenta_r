package com.onlinerental.repository;

import com.onlinerental.domain.Product;
import com.onlinerental.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByOwner(User owner);

    @Query("""
            SELECT DISTINCT p FROM Product p
            WHERE (:category IS NULL OR p.category = :category)
            AND (:brand IS NULL OR p.brand = :brand)
            AND (:model IS NULL OR p.model = :model)
            ORDER BY p.createdAt DESC
            """)
    List<Product> findWithFilters(
            @Param("category") String category,
            @Param("brand") String brand,
            @Param("model") String model
    );

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE (:category IS NULL OR p.category = :category) AND p.brand IS NOT NULL ORDER BY p.brand")
    List<String> findDistinctBrands(@Param("category") String category);

    @Query("""
            SELECT DISTINCT p.model FROM Product p
            WHERE (:category IS NULL OR p.category = :category)
            AND (:brand IS NULL OR p.brand = :brand)
            AND p.model IS NOT NULL
            ORDER BY p.model
            """)
    List<String> findDistinctModels(@Param("category") String category, @Param("brand") String brand);
}
