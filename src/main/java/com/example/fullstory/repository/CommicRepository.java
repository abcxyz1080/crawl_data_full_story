package com.example.fullstory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullstory.model.Commic;

@Repository

public interface CommicRepository extends JpaRepository<Commic, Long> {
	Commic findByTitle(String title);

	@Modifying
	@Transactional
	@Query(value = "update commic c set c.number_of_chapter =?1 where c.title = ?2", nativeQuery = true)
	public void updateNumberOfChapter(String numberOfChapter, String title);
}
