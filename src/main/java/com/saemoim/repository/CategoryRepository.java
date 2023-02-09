package com.saemoim.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saemoim.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findById(Long id);
}
