package com.example.fullstory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fullstory.model.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

}
