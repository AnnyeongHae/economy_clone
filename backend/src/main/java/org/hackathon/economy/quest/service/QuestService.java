package org.hackathon.economy.quest.service;

import lombok.RequiredArgsConstructor;
import org.hackathon.economy.account.domain.Account;
import org.hackathon.economy.quest.domain.QuestAchieve;
import org.hackathon.economy.quest.repository.QuestAchieveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestService {


//    public List<QuestAchieve> getQuest(Account account) {
//
//    }
}
