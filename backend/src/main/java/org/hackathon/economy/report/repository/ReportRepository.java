package org.hackathon.economy.report.repository;

import org.hackathon.economy.account.domain.DailyInterest;
import org.hackathon.economy.report.domain.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ReportRepository extends JpaRepository<DailyInterest, String> {
    @Query(value = "SELECT m.member_name, m.member_grade, m.member_point FROM MEMBER m JOIN ACCOUNT a ON m.member_no = a.member_no WHERE a.account_no = :accountNo", nativeQuery = true)
    List<Object[]> findMemberGradeAndScoreByAccountNo(@Param("accountNo") Long accountNo);

    @Query("SELECT di FROM DailyInterest di JOIN FETCH di.account a WHERE a.accountNo = :accountNo AND di.todayDate BETWEEN :startDate AND :endDate ORDER BY di.todayDate DESC")
    Page<DailyInterest> findAllInterestHistory(
            @Param("accountNo") Long accountNo,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query(value = """
    SELECT 
        DATE(di.today_date) as date,
        CAST(SUM(di.today_interest) as DECIMAL(10,2)) as totalInterest,
        CAST(AVG(di.today_balance) as DECIMAL(10,2)) as avgBalance,
        COUNT(DISTINCT DATE(di.today_date)) as totalDays,
        COALESCE(COUNT(DISTINCT qa.achieve_no), 0) as questCount,
        COALESCE(SUM(q.quest_point), 0) as totalScore
    FROM DAILY_INTEREST di
    JOIN ACCOUNT a ON di.account_no = a.account_no
    JOIN MEMBER m ON a.member_no = m.member_no
    LEFT JOIN QUEST_ACHIEVE qa ON m.member_no = qa.member_no 
        AND DATE(di.today_date) = DATE(qa.achieve_date_time)
    LEFT JOIN QUEST q ON qa.quest_no = q.quest_no
    WHERE a.account_no = :accountNo
        AND di.today_date BETWEEN :startDate AND :endDate
    GROUP BY DATE(di.today_date)
    ORDER BY date ASC
    """, nativeQuery = true)
    List<Object[]> getDailyIntegratedData(
            @Param("accountNo") Long accountNo,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT 
        MIN(DATE(di.today_date)) as date,
        CAST(SUM(di.today_interest) as DECIMAL(10,2)) as totalInterest,
        CAST(AVG(di.today_balance) as DECIMAL(10,2)) as avgBalance,
        COUNT(DISTINCT DATE(di.today_date)) as totalDays,
        COALESCE(COUNT(DISTINCT qa.achieve_no), 0) as questCount,
        COALESCE(SUM(q.quest_point), 0) as totalScore
    FROM DAILY_INTEREST di
    JOIN ACCOUNT a ON di.account_no = a.account_no
    JOIN MEMBER m ON a.member_no = m.member_no
    LEFT JOIN QUEST_ACHIEVE qa ON m.member_no = qa.member_no 
        AND YEARWEEK(di.today_date, 1) = YEARWEEK(qa.achieve_date_time, 1)
    LEFT JOIN QUEST q ON qa.quest_no = q.quest_no
    WHERE a.account_no = :accountNo
        AND di.today_date BETWEEN :startDate AND :endDate
    GROUP BY YEARWEEK(di.today_date, 1)
    ORDER BY date ASC
    """, nativeQuery = true)
    List<Object[]> getWeeklyIntegratedData(
            @Param("accountNo") Long accountNo,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT 
        LAST_DAY(di.today_date) as date,
        CAST(SUM(di.today_interest) as DECIMAL(10,2)) as totalInterest,
        CAST(AVG(di.today_balance) as DECIMAL(10,2)) as avgBalance,
        COUNT(DISTINCT DATE(di.today_date)) as totalDays,
        COALESCE(COUNT(DISTINCT qa.achieve_no), 0) as questCount,
        COALESCE(SUM(q.quest_point), 0) as totalScore
    FROM DAILY_INTEREST di
    JOIN ACCOUNT a ON di.account_no = a.account_no
    JOIN MEMBER m ON a.member_no = m.member_no
    LEFT JOIN QUEST_ACHIEVE qa ON m.member_no = qa.member_no 
        AND YEAR(di.today_date) = YEAR(qa.achieve_date_time) 
        AND MONTH(di.today_date) = MONTH(qa.achieve_date_time)
    LEFT JOIN QUEST q ON qa.quest_no = q.quest_no
    WHERE a.account_no = :accountNo
        AND di.today_date BETWEEN :startDate AND :endDate
    GROUP BY YEAR(di.today_date), MONTH(di.today_date)
    ORDER BY date ASC
    """, nativeQuery = true)
    List<Object[]> getMonthlyIntegratedData(
            @Param("accountNo") Long accountNo,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT 
        q.quest_content,
        q.quest_point,
        qa.achieve_date_time,
        q.quest_type
    FROM ACCOUNT a
    JOIN MEMBER m ON a.member_no = m.member_no
    JOIN QUEST_ACHIEVE qa ON m.member_no = qa.member_no
    JOIN QUEST q ON qa.quest_no = q.quest_no
    WHERE a.account_no = :accountNo
        AND DATE(qa.achieve_date_time) = :achieveDate
    ORDER BY qa.achieve_date_time DESC, q.quest_type ASC
    """, nativeQuery = true)
    List<Object[]> findQuestDetailsByDate(
            @Param("accountNo") Long accountNo,
            @Param("achieveDate") String achieveDate
    );

    @Query(value = """
    SELECT COUNT(*) 
    FROM DAILY_INTEREST di
    WHERE di.account_no = :accountNo 
    AND di.today_date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    Long countDailyInterestData(
            @Param("accountNo") Long accountNo,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}