package org.hackathon.economy.qrsession.service;


import lombok.AllArgsConstructor;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.repository.MemberRepository;
import org.hackathon.economy.qrsession.domain.Session;
import org.hackathon.economy.qrsession.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final MemberRepository memberRepository;


    // 세션 생성
    public String createSession(Long memberNo) {
        // MemberRepository에서 Member 조회
        Member member = memberRepository.findOne(memberNo);
        if (member == null) {
            throw new IllegalArgumentException("Member not found");
        }

        // 세션 생성
        String sessionKey = UUID.randomUUID().toString();
        Session session = new Session();
        session.setSessionKey(sessionKey);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        session.setMember(member);

        // 세션 저장
        sessionRepository.save(session);
        return sessionKey;
    }

    // 세션 유효성 검사
    public boolean isSessionValid(String sessionKey) {
        Session session = sessionRepository.findBySessionKey(sessionKey);
        return session != null && session.getExpiresAt().isAfter(LocalDateTime.now());
    }


    // 세션 갱신
    public String refreshSession(String sessionKey) {
        Session session = sessionRepository.findBySessionKey(sessionKey);
        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        // 세션 만료 시간 갱신
        session.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        sessionRepository.save(session);
        return session.getSessionKey();
    }

    // 만료된 세션 조회
    public List<Session> findExpiredSessions() {
        return sessionRepository.findExpiredSessions(LocalDateTime.now());
    }

    // 만료된 세션 삭제
    public void deleteSessionById(Long sessionId) {
        sessionRepository.deleteExpiredSessions(LocalDateTime.now());
    }

    public Long getRemainingTime(String sessionKey) {
        Session session = sessionRepository.findBySessionKey(sessionKey);
        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        // 현재 시간과 만료 시간의 차이를 계산
        LocalDateTime now = LocalDateTime.now();
        return Math.max(0, java.time.Duration.between(now, session.getExpiresAt()).getSeconds());
    }
}
