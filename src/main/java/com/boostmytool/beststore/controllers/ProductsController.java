package com.boostmytool.beststore.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.boostmytool.beststore.models.Product;
import com.boostmytool.beststore.models.ProductDto;
import com.boostmytool.beststore.services.ProductRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductRepository repo;

    // Show all products
    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = repo.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }

    // Show create form
    @GetMapping("/create")
    public String showCreatePage(Model model) {
        model.addAttribute("productDto", new ProductDto());
        return "products/CreateProduct";
    }

    // Handle create POST
    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult result,
            Model model) {

        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image is required!"));
        }

        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception ex) {
            System.out.println("Image Upload Exception: " + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        repo.save(product);
        return "redirect:/products";
    }

    // Show edit page
    @GetMapping("/edit/{id}")
    public String showEditPage(@PathVariable("id") int id, Model model) {
        try {
            Product product = repo.findById(id).orElseThrow();
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("product", product);
            model.addAttribute("productDto", productDto);
            return "products/EditProducts";

        } catch (Exception ex) {
            System.out.println("Edit Page Exception: " + ex.getMessage());
            return "redirect:/products";
        }
    }

    // Handle update
    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable("id") int id,
            @Valid @ModelAttribute("productDto") ProductDto productDto,
            BindingResult result,
            Model model) {

        try {
            Product product = repo.findById(id).orElseThrow();

            if (result.hasErrors()) {
                model.addAttribute("product", product);
                return "products/EditProducts";
            }

            // Update image if new image provided
            if (!productDto.getImageFile().isEmpty()) {
                String uploadDir = "public/images/";

                // Delete old image
                Path oldImagePath = Paths.get(uploadDir).resolve(product.getImageFileName());
                try {
                    Files.deleteIfExists(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Old Image Delete Exception: " + ex.getMessage());
                }

                // Save new image
                MultipartFile image = productDto.getImageFile();
                String storageFileName = new Date().getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir).resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
                    product.setImageFileName(storageFileName);
                } catch (Exception ex) {
                    System.out.println("New Image Upload Exception: " + ex.getMessage());
                }
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            repo.save(product);

        } catch (Exception ex) {
            System.out.println("Update Exception: " + ex.getMessage());
        }

        return "redirect:/products";
    }

    // Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        try {
            Product product = repo.findById(id).orElseThrow();
            Path imagePath = Paths.get("public/images/").resolve(product.getImageFileName());

            try {
                Files.deleteIfExists(imagePath);
            } catch (Exception ex) {
                System.out.println("Image Delete Exception: " + ex.getMessage());
            }

            repo.delete(product);

        } catch (Exception ex) {
            System.out.println("Delete Exception: " + ex.getMessage());
        }

        return "redirect:/products";
    }
}
