package org.hackathon.economy.quest.service;

import lombok.RequiredArgsConstructor;
import org.hackathon.economy.account.domain.Account;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.domain.QuestAchieve;
import org.hackathon.economy.quest.repository.QuestAchieveRepository;
import org.hackathon.economy.quest.repository.QuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;

    public List<Quest> getActiveQuests() {
        return questRepository.getActiveQuests();
    }

    public List<Quest> getActiveDailyQuests() {
        return questRepository.getActiveDailyQuests();
    }

    public List<Quest> getActiveWeeklyQuests() {
        return questRepository.getActiveWeeklyQuests();
    }

    public List<Quest> getActiveMonthlyQuests() {
        return questRepository.getActiveMonthlyQuests();
    }

    public Quest getRecommendQuest() { return questRepository.getRecommendQuest(); }
}
