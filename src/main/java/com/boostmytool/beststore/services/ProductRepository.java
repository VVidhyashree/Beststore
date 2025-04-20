package com.boostmytool.beststore.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boostmytool.beststore.models.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

}
