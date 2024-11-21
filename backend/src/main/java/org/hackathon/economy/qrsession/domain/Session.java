package org.hackathon.economy.qrsession.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hackathon.economy.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue
    @Column(name="session_id")
    private Long sessionId; // 세션 ID (Primary Key)

    @Column(name="session_key", nullable = false)
    private String sessionKey; // 세션 고유 키(UUID)

    @Column(name="expires_at", nullable = false)
    private LocalDateTime expiresAt; // 만료 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false) // Member와 연관
    private Member member;
}