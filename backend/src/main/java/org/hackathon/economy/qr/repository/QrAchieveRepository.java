package org.hackathon.economy.qr.repository;

import org.hackathon.economy.qr.domain.QrAchieve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QrAchieveRepository extends JpaRepository<QrAchieve, Long> {

    @Query(value = """
        SELECT COUNT(*)
        FROM qr_achieve
        WHERE member_no = :memberNo
          AND quest_no = :questNo
        """, nativeQuery = true)
    Integer countRecentAchievement(@Param("memberNo") Long memberNo, @Param("questNo") Long questNo);

    @Query(value = """
        SELECT *
        FROM qr_achieve
        WHERE member_no = :memberNo
    """, nativeQuery = true)
    List<QrAchieve> getTotal(@Param("memberNo") Long memberNo);

    @Query(value = """
        SELECT *
        FROM qr_achieve
        WHERE member_no = :memberNo
        AND DATE_FORMAT(achieve_date_time, "%Y-%m-%d") = CURDATE()
    """, nativeQuery = true)
    List<QrAchieve> getToday(@Param("memberNo") Long memberNo);

    List<QrAchieve> findByMemberNoAndAchieveDateTimeBetween(Long memberNo, LocalDateTime start, LocalDateTime end);

}
