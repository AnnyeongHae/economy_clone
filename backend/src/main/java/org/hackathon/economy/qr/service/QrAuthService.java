package org.hackathon.economy.qr.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.domain.QuestAchieve;
import org.hackathon.economy.quest.repository.QuestAchieveRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class QrAuthService {
    private final QuestAchieveRepository questAchieveRepository; // 퀘스트 달성 저장소

    // qr 인증 처리 메서드
    public void verifyQrQuest(Quest quest, Member member) {
        // 퀘스트 달성 여부 확인
        Integer count = questAchieveRepository.countAchievements(member.getMemberNo(), quest.getQuestNo());
        if (count > 0) {
            throw new IllegalStateException("이미 해당 퀘스트를 완료했습니다.");
        }

        // 퀘스트 달성 기록 생성
        QuestAchieve achieve = new QuestAchieve();
        achieve.setMember(member); // 달성한 회원
        achieve.setAchieveDateTime(Timestamp.valueOf(LocalDateTime.now())); // 달성 시간
        achieve.setQuest(quest); // 달성한 퀘스트

        // 퀘스트 달성 저장
        questAchieveRepository.save(achieve);
    }
}
