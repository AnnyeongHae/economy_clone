package org.hackathon.economy.greenttransaction.service;

import lombok.RequiredArgsConstructor;
import org.hackathon.economy.greenttransaction.domain.GreenTransaction;
import org.hackathon.economy.greenttransaction.repostiory.CardRepository;
import org.hackathon.economy.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Map<String, Long> getCount(Member member) {
        return cardRepository.getCount(member);
    }
}
