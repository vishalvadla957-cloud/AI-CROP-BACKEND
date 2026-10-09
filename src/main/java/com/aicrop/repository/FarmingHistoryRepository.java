package com.aicrop.repository;

import com.aicrop.model.FarmingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmingHistoryRepository extends JpaRepository<FarmingHistory, Long> {
    List<FarmingHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<FarmingHistory> findByUserIdAndRecommendedCropOrderByCreatedAtDesc(Long userId, String crop);

    @Query("SELECT h.recommendedCrop, COUNT(h) as cnt FROM FarmingHistory h WHERE h.user.id = :userId GROUP BY h.recommendedCrop ORDER BY cnt DESC")
    List<Object[]> findCropFrequencyByUserId(Long userId);

    long countByUserId(Long userId);
}
