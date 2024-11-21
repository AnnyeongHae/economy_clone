package org.hackathon.economy.quest.repository;

import org.hackathon.economy.quest.domain.QuestAchieve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestAchieveRepository extends JpaRepository<QuestAchieve, Long> {

    @Query(value = """
        SELECT COUNT(*)
        FROM quest_achieve
        WHERE member_no = :memberNo
          AND quest_no = :questNo
          AND achieve_date_time BETWEEN (NOW() - INTERVAL 30 SECOND) AND NOW()
        """, nativeQuery = true)
    Integer countRecentAchievement(@Param("memberNo") Long memberNo, @Param("questNo") Long questNo);

    @Query(value = """
        SELECT *
        FROM quest_achieve
        WHERE member_no = :memberNo
    """, nativeQuery = true)
    List<QuestAchieve> getTotal(@Param("memberNo") Long memberNo);

    @Query(value = """
        SELECT *
        FROM quest_achieve
        WHERE member_no = :memberNo
        AND DATE_FORMAT(achieve_date_time, "%Y-%m-%d") = CURDATE()
    """, nativeQuery = true)
    List<QuestAchieve> getToday(@Param("memberNo") Long memberNo);

    @Query(value = """
        SELECT *
        FROM quest_achieve
        WHERE member_no = :memberNo
        AND 
          date_format(achieve_date_time,'%Y-%m-%d')
        BETWEEN
         (SELECT ADDDATE(CURDATE(), - WEEKDAY(CURDATE()) + 0 ))
        AND
         (SELECT ADDDATE(CURDATE(), - WEEKDAY(CURDATE()) + 6 ))
    """, nativeQuery = true)
    List<QuestAchieve> getWeek(@Param("memberNo") Long memberNo);

    @Query(value = """
        SELECT *
        FROM quest_achieve
        WHERE member_no = :memberNo
        AND 
          date_format(achieve_date_time,'%Y-%m-%d')
        between 
          date_format(now(), '%Y-%m-01') 
        and 
          date_format(now(), '%Y-%m-%d')
    """, nativeQuery = true)
    List<QuestAchieve> getMonth(@Param("memberNo") Long memberNo);
}
