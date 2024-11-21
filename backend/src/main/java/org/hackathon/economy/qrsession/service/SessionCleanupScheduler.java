package org.hackathon.economy.qrsession.service;

import org.hackathon.economy.qrsession.domain.Session;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionCleanupScheduler {

    private final SessionService sessionService;

    public SessionCleanupScheduler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void cleanupExpiredSessions() {
        List<Session> expiredSessions = sessionService.findExpiredSessions();
        if (expiredSessions.isEmpty()) {
            return; // 만료된 세션이 없으면 작업 종료
        }
        expiredSessions.forEach(session -> sessionService.deleteSessionById(session.getSessionId()));
    }

}
