package com.example.fullstory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fullstory.model.Story;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
	Story findByTitleLike(String title);

}
