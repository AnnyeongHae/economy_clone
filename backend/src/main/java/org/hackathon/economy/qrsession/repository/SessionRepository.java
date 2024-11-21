package org.hackathon.economy.qrsession.repository;

import org.hackathon.economy.qrsession.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    // sessionKey로 세션 조회
    @Query(value = "SELECT * FROM session WHERE session_key = :sessionKey", nativeQuery = true)
    Session findBySessionKey(@Param("sessionKey") String sessionKey);

    // 특정 회원의 활성 세션 조회
    @Query(value = """
            SELECT * FROM session 
            WHERE member_no = :memberNo AND expires_at > :currentTime
            """, nativeQuery = true)
    List<Session> findActiveSessionsByMemberNo(@Param("memberNo") Long memberNo, @Param("currentTime") LocalDateTime currentTime);

    //    // 만료된 세션 삭제
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM session WHERE expires_at < :currentTime", nativeQuery = true)
    void deleteExpiredSessions(@Param("currentTime") LocalDateTime currentTime);

    // 만료된 세션 조회
    @Query(value = "SELECT * FROM session WHERE expires_at < :currentTime", nativeQuery = true)
    List<Session> findExpiredSessions(@Param("currentTime") LocalDateTime currentTime);

}
