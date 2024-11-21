package org.hackathon.economy.qr.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.qr.domain.QrAchieve;
import org.hackathon.economy.qr.repository.QrAchieveRepository;
import org.hackathon.economy.qrsession.domain.Session;
import org.hackathon.economy.qrsession.repository.SessionRepository;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.domain.QuestAchieve;
import org.hackathon.economy.quest.repository.QuestAchieveRepository;
import org.hackathon.economy.quest.repository.QuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class QrAchieveService {

    private final SessionRepository sessionRepository;
    private final QuestRepository questRepository;
    private final QrAchieveRepository qrAchieveRepository;
    private final QuestAchieveRepository questAchieveRepository;
    private final SessionValidationService sessionValidationService;

    public void verifyAndSaveQrAchieve(String sessionKey, Long questNo, HttpSession httpSession) {
        // 현재 로그인된 사용자 정보 가져오기
        String loggedInMemberEmail = (String) httpSession.getAttribute("memberEmail");
        if (loggedInMemberEmail == null) {
            throw new IllegalStateException("로그인되지 않은 사용자입니다.");
        }

        // 현재 로그인한 멤버 조회
        Member loggedInMember = sessionValidationService.getMemberByEmail(loggedInMemberEmail);

        // QR 세션 검증
        Session session = sessionValidationService.validateSession(sessionKey);

        // 요청된 퀘스트 검증
        Quest quest = sessionValidationService.validateQuest(questNo);

        // QR 인증 데이터 저장
        QrAchieve qrAchieve = new QrAchieve();
        qrAchieve.setAchieveDateTime(LocalDateTime.now());
        qrAchieve.setMemberNo(loggedInMember.getMemberNo()); // 현재 로그인한 멤버의 memberNo 설정
        qrAchieve.setQuestNo(quest.getQuestNo());
        qrAchieve.setSessionId(session.getSessionId());
        qrAchieve.setCreatorMemberNo(session.getMember().getMemberNo()); // QR 세션 생성자의 memberNo 설정

        qrAchieveRepository.save(qrAchieve);

        // QuestAchieve 테이블 갱신
        saveQuestAchievement(loggedInMember, quest);
    }



    private void saveQuestAchievement(Member member, Quest quest) {
        // 중복 방지를 위해 이미 완료된 퀘스트인지 확인
        Integer achievementCount = questAchieveRepository.countAchievements(member.getMemberNo(), quest.getQuestNo());
        if (achievementCount > 0) {
            throw new IllegalStateException("이미 해당 퀘스트를 완료했습니다.");
        }

        // 퀘스트 완료 데이터 저장
        QuestAchieve questAchieve = new QuestAchieve();
        questAchieve.setMember(member); // 기존 Member 객체 사용
        questAchieve.setQuest(quest); // 퀘스트 정보 매핑
        questAchieve.setAchieveDateTime(Timestamp.valueOf(LocalDateTime.now()));

        questAchieveRepository.save(questAchieve);
    }
}
