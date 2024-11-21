package org.hackathon.economy.quest.service;

import lombok.RequiredArgsConstructor;
import org.hackathon.economy.account.domain.Account;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.quest.domain.QuestAchieve;
import org.hackathon.economy.quest.repository.QuestAchieveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestAchieveService {

    private final QuestAchieveRepository questAchieveRepository;

    public List<QuestAchieve> getTotal(Member member) {
        return questAchieveRepository.getTotal(member.getMemberNo());
    }

    public List<QuestAchieve> getToday(Member member) {
        return questAchieveRepository.getToday(member.getMemberNo());
    }

    public List<QuestAchieve> getWeek(Member member) {
        return questAchieveRepository.getWeek(member.getMemberNo());
    }

    public List<QuestAchieve> getMonth(Member member) {
        return questAchieveRepository.getMonth(member.getMemberNo());
    }
}
