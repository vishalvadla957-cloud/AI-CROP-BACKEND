package com.aicrop.repository;

import com.aicrop.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    List<Disease> findByAffectedCropIgnoreCase(String crop);
    List<Disease> findByCategory(String category);

    @Query("SELECT d FROM Disease d WHERE LOWER(d.symptoms) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Disease> searchBySymptomOrName(String keyword);
}
