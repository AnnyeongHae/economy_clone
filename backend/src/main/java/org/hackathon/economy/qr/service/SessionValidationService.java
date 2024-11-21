package org.hackathon.economy.qr.service;

import lombok.RequiredArgsConstructor;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.repository.MemberRepository;
import org.hackathon.economy.qrsession.domain.Session;
import org.hackathon.economy.qrsession.repository.SessionRepository;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.repository.QuestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessionValidationService {
    private final SessionRepository sessionRepository;
    private final QuestRepository questRepository;
    private final MemberRepository memberRepository;

    public Session validateSession(String sessionKey) {
        Session session = sessionRepository.findBySessionKey(sessionKey);
        if (session == null || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("유효하지 않은 세션입니다.");
        }
        return session;
    }

    public Quest validateQuest(Long questNo) {
        return questRepository.findByNo(questNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 퀘스트입니다."));
    }

    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 멤버입니다.");
        }
        return member;
    }
}
