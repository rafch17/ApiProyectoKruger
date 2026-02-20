package com.example.demo.resolver;

import com.example.demo.dto.ProductInput;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductResolver {

    private final ProductService productService;

    public ProductResolver(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Product> products() {
        return productService.getAllProducts();
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Product product(@Argument Long id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Product createProduct(@Argument ProductInput input) {
        Product product = new Product();
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        return productService.createProduct(product);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Product updateProduct(@Argument Long id, @Argument ProductInput input) {
        Product productData = new Product();
        productData.setName(input.getName());
        productData.setDescription(input.getDescription());
        productData.setPrice(input.getPrice());
        return productService.updateProduct(id, productData);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean deleteProduct(@Argument Long id) {
        return productService.deleteProduct(id);
    }
}
