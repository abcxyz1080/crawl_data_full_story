package com.example.fullstory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fullstory.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
