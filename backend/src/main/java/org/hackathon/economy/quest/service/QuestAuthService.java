package org.hackathon.economy.quest.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hackathon.economy.account.domain.Interest;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.repository.MemberRepository;
import org.hackathon.economy.member.service.MemberService;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.domain.QuestAchieve;
import org.hackathon.economy.quest.repository.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestAuthService {
    private final QuestRepositoryInterface questRepositoryInterface;
    private final GreenTransactionRepository greenTransactionRepository;
    private final QuestAchieveRepository questAchieveRepository;
    private final InterestRepository interestRepository;
    private final MemberAuthRepository memberAuthRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public void processQuestAchievements(Long memberNo) {
        // 멤버 객체 가져오기
        Member member = memberAuthRepository.findById(memberNo).orElseThrow();

        List<Quest> activeQuests = questRepositoryInterface.findQuestsByIsActiveAndQuestTypeOrderByQuestFrequencyAsc(true, 1);

        for (Quest quest : activeQuests) {
            // 이미 달성한 퀘스트인지 확인
            if (questAchieveRepository.existsByQuestAndMember(quest, member)) {
                continue;
            }

            // 트랜잭션 카테고리 count 확인
            String questContent = quest.getQuestContent().trim();
            Long transactionCount = greenTransactionRepository.countByTransactionCategory(questContent);

            System.out.println("questContent = " + questContent);
            System.out.println("transactionCount = " + transactionCount);
            if (transactionCount >= quest.getQuestCount()) {
                // Quest 달성 처리
                createQuestAchievement(quest, member);

                // Interest 상태 업데이트
                updateInterestStatus(member, quest.getQuestType());

                // 포인트 적립
                updateMemberPoint(member, quest.getQuestPoint());
            }
        }
    }

    private void createQuestAchievement(Quest quest, Member member) {
        System.out.println("quest = " + quest);
        System.out.println("member = " + member);
        QuestAchieve questAchieve = new QuestAchieve();
        questAchieve.setMember(member);
        questAchieve.setQuest(quest);
        questAchieve.setAchieveDateTime(new Timestamp(System.currentTimeMillis()));
        questAchieveRepository.save(questAchieve);
    }

    private void updateInterestStatus(Member member, Integer questType) {
        Interest interest = interestRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("Interest not found"));

        switch (questType) {
            case 0:
                interest.setCurrentDaily(true);
                break;
            case 1:
                interest.setCurrentWeekly(true);
                break;
            case 2:
                interest.setCurrentMonthly(true);
                break;
            default:
                throw new IllegalArgumentException("Invalid quest type");
        }

        interestRepository.save(interest);
    }

    private void updateMemberPoint(Member member, Integer questPoint) {
        member.setMemberPoint(member.getMemberPoint() + questPoint);
        Member newMember = memberRepository.update(member);
        memberService.updateGrade(newMember.getMemberPoint(), newMember.getMemberNo());
    }
}