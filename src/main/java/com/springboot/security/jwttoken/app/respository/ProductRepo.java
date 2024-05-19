package com.springboot.security.jwttoken.app.respository;

import com.springboot.security.jwttoken.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Integer> {
}