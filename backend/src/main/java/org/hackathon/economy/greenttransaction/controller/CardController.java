package org.hackathon.economy.greenttransaction.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hackathon.economy.greenttransaction.domain.GreenTransaction;
import org.hackathon.economy.greenttransaction.service.CardService;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/card")
@Slf4j
public class CardController {

    private final CardService cardService;
    private final AuthenticationService authenticationService;

    // 진행 퀘스트 중 거래 완료 횟수
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCount(HttpSession session) {
        try {
            Member member = authenticationService.getAuthenticatedMember(session);
            return ResponseEntity.ok(cardService.getCount(member));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
