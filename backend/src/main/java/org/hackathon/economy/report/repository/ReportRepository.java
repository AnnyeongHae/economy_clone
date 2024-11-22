package org.hackathon.economy.report.repository;

import org.hackathon.economy.account.domain.DailyInterest;
import org.hackathon.economy.report.domain.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ReportRepository extends JpaRepository<DailyInterest, Long> { // String -> Long 변경
    @Query(value = "SELECT m.member_name, m.member_grade, m.member_point FROM member m JOIN account a ON m.member_no = a.member_no WHERE a.account_no = :accountNo", nativeQuery = true)
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
        COALESCE(CAST(SUM(di.today_interest) as DECIMAL(10,2)), 0.00) as totalInterest,
        COALESCE(CAST(AVG(di.today_balance) as DECIMAL(10,2)), 0.00) as avgBalance,
        COALESCE(COUNT(DISTINCT DATE(di.today_date)), 0) as totalDays,
        COALESCE(COUNT(DISTINCT qa.achieve_no), 0) as questCount,
        COALESCE(SUM(q.quest_point), 0) as totalScore
    FROM daily_interest di
    LEFT JOIN account a ON di.account_no = a.account_no
    LEFT JOIN member m ON a.member_no = m.member_no
    LEFT JOIN quest_achieve qa ON m.member_no = qa.member_no 
        AND DATE(di.today_date) = DATE(qa.achieve_date_time)
    LEFT JOIN quest q ON qa.quest_no = q.quest_no
    WHERE di.account_no = :accountNo
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
        MIN(di.today_date) as date,
        COALESCE(CAST(SUM(di.today_interest) as DECIMAL(10,2)), 0.00) as totalInterest,
        COALESCE(CAST(AVG(di.today_balance) as DECIMAL(10,2)), 0.00) as avgBalance,
        COALESCE(COUNT(DISTINCT DATE(di.today_date)), 0) as totalDays,
        COALESCE(COUNT(DISTINCT qa.achieve_no), 0) as questCount,
        COALESCE(SUM(q.quest_point), 0) as totalScore
    FROM daily_interest di
    LEFT JOIN account a ON di.account_no = a.account_no
    LEFT JOIN member m ON a.member_no = m.member_no
    LEFT JOIN quest_achieve qa ON m.member_no = qa.member_no 
        AND YEARWEEK(di.today_date, 1) = YEARWEEK(qa.achieve_date_time, 1)
    LEFT JOIN quest q ON qa.quest_no = q.quest_no
    WHERE di.account_no = :accountNo
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
        MAX(LAST_DAY(di.today_date)) as date,
        COALESCE(CAST(SUM(di.today_interest) as DECIMAL(10,2)), 0.00) as totalInterest,
        COALESCE(CAST(AVG(di.today_balance) as DECIMAL(10,2)), 0.00) as avgBalance,
        COALESCE(COUNT(DISTINCT DATE(di.today_date)), 0) as totalDays,
        COALESCE(COUNT(DISTINCT qa.achieve_no), 0) as questCount,
        COALESCE(SUM(q.quest_point), 0) as totalScore
    FROM daily_interest di
    LEFT JOIN account a ON di.account_no = a.account_no
    LEFT JOIN member m ON a.member_no = m.member_no
    LEFT JOIN quest_achieve qa ON m.member_no = qa.member_no 
        AND YEAR(di.today_date) = YEAR(qa.achieve_date_time) 
        AND MONTH(di.today_date) = MONTH(qa.achieve_date_time)
    LEFT JOIN quest q ON qa.quest_no = q.quest_no
    WHERE di.account_no = :accountNo
        AND di.today_date BETWEEN :startDate AND :endDate
    GROUP BY YEAR(di.today_date), MONTH(di.today_date)
    ORDER BY MAX(di.today_date) ASC
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
    FROM account a
    LEFT JOIN member m ON a.member_no = m.member_no
    LEFT JOIN quest_achieve qa ON m.member_no = qa.member_no
    LEFT JOIN quest q ON qa.quest_no = q.quest_no
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
    FROM daily_interest di
    WHERE di.account_no = :accountNo 
    AND di.today_date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    Long countDailyInterestData(
            @Param("accountNo") Long accountNo,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}