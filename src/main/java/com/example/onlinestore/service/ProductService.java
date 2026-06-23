package com.example.onlinestore.service;

import com.example.onlinestore.dto.CategoryResponse;
import com.example.onlinestore.dto.PagedResponse;
import com.example.onlinestore.dto.ProductRequest;
import com.example.onlinestore.dto.ProductResponse;
import com.example.onlinestore.entity.Category;
import com.example.onlinestore.entity.Product;
import com.example.onlinestore.exception.ResourceNotFoundException;
import com.example.onlinestore.repository.CategoryRepository;
import com.example.onlinestore.repository.ProductRepository;
import com.example.onlinestore.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена"));

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setActive(true);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PagedResponse<ProductResponse> searchProducts(
            String keyword,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean active,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        validatePagination(page, size);

        String safeSortBy = validateSortBy(sortBy);

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, safeSortBy)
        );

        Specification<Product> specification = Specification
                .where(ProductSpecification.hasKeyword(keyword))
                .and(ProductSpecification.hasCategory(categoryId))
                .and(ProductSpecification.priceGreaterThanOrEqual(minPrice))
                .and(ProductSpecification.priceLessThanOrEqual(maxPrice))
                .and(ProductSpecification.isActive(active));

        Page<Product> productPage = productRepository.findAll(specification, pageable);

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PagedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));

        return toResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return toResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));

        productRepository.delete(product);
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Номер страницы не может быть меньше 0");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Размер страницы должен быть от 1 до 100");
        }
    }

    private String validateSortBy(String sortBy) {
        Set<String> allowedSortFields = Set.of(
                "id",
                "name",
                "price",
                "stockQuantity",
                "createdAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Недопустимое поле сортировки. Можно: id, name, price, stockQuantity, createdAt"
            );
        }

        return sortBy;
    }

    private ProductResponse toResponse(Product product) {
        Category category = product.getCategory();

        CategoryResponse categoryResponse = new CategoryResponse(
                category.getId(),
                category.getName()
        );

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getActive(),
                product.getCreatedAt(),
                categoryResponse
        );
    }
}