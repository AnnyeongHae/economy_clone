package org.hackathon.economy.quest.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hackathon.economy.quest.domain.ActivatedQuest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActivatedQuestRepository {

    @PersistenceContext
    private EntityManager em;

    public List<ActivatedQuest> findAll() {
        return em.createQuery("select a from ActivatedQuest a", ActivatedQuest.class).getResultList();
    }

    // 주기별 자동 교체


}
