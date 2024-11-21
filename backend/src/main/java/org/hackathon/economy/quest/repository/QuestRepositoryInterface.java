package org.hackathon.economy.quest.repository;

import org.hackathon.economy.account.domain.DailyInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface QuestRepositoryInterface extends JpaRepository<DailyInterest, Long> {

}
