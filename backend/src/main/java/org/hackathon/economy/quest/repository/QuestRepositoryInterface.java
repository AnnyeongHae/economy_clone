package org.hackathon.economy.quest.repository;

import org.hackathon.economy.account.domain.DailyInterest;
import org.hackathon.economy.quest.domain.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QuestRepositoryInterface extends JpaRepository<Quest, Long> {
    //List<Quest> findQuestsByIsActiveNotAndQuestTypeOrderByQuestFrequencyAsc(int questType);
    List<Quest> findByIsActiveAndIsQr(boolean isActive, boolean isQr);
    List<Quest> findQuestsByIsActiveAndQuestTypeOrderByQuestFrequencyAsc(boolean isActive, int questType);
}