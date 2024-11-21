package org.hackathon.economy.greenttransaction.repostiory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hackathon.economy.greenttransaction.domain.GreenTransaction;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.quest.domain.Quest;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class CardRepository {

    @PersistenceContext
    private EntityManager em;

    public Map<String, Long> getCount(Member member) {
        List<String> activeQuestContents = em.createQuery(
                        "SELECT q.questContent FROM Quest q WHERE q.isActive = true", String.class)
                .getResultList();

        // 2. transactionCategory별 개수 계산
        List<Object[]> resultList = em.createQuery(
                        "SELECT gt.transactionCategory, COUNT(gt) " +
                                "FROM GreenTransaction gt " +
                                "WHERE gt.transactionCategory in :activeQuestContents " +
                                "AND gt.member = :member " +
                                "GROUP BY gt.transactionCategory", Object[].class)
                .setParameter("activeQuestContents", activeQuestContents)
                .setParameter("member", member)
                .getResultList();

        log.info("hereeeeeeeeeeeeee : {}", resultList.size());

        // 3. 결과를 Map으로 변환
        Map<String, Long> resultMap = new HashMap<>();
        for (Object[] result : resultList) {
            String transactionCategory = (String) result[0];
            Long count = (Long) result[1];
            resultMap.put(transactionCategory, count);
        }

        return resultMap;
    }
}
