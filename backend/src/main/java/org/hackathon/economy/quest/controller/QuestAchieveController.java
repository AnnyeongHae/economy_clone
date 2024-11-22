package org.hackathon.economy.quest.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hackathon.economy.account.domain.Account;
import org.hackathon.economy.account.service.AccountService;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.member.service.AuthenticationService;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.domain.QuestAchieve;
import org.hackathon.economy.quest.service.QuestAchieveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/achieve")
public class QuestAchieveController {

    private final AuthenticationService authenticationService;
    private final QuestAchieveService questAchieveService;

    /* 완료된 퀘스트 조회 */
    // 전체
    @GetMapping("/total")
    public ResponseEntity<List<Optional<Quest>>> getTotal(HttpSession session) {
        try {
            Member member = authenticationService.getAuthenticatedMember(session);
            List<Optional<Quest>> totalQuests = questAchieveService.getTotal(member);
            return ResponseEntity.ok(totalQuests);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // 일일
    @GetMapping("/today")
    public ResponseEntity<List<QuestAchieve>> getToday(HttpSession session) {
        try {
            Member member = authenticationService.getAuthenticatedMember(session);
            List<QuestAchieve> todayQuests = questAchieveService.getToday(member);
            return ResponseEntity.ok(todayQuests);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // 주간
    @GetMapping("/week")
    public ResponseEntity<List<QuestAchieve>> getWeek(HttpSession session) {
        try {
            Member member = authenticationService.getAuthenticatedMember(session);
            List<QuestAchieve> weekQuests = questAchieveService.getWeek(member);
            return ResponseEntity.ok(weekQuests);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // 월간
    @GetMapping("/month")
    public ResponseEntity<List<QuestAchieve>> getMonth(HttpSession session) {
        try {
            Member member = authenticationService.getAuthenticatedMember(session);
            List<QuestAchieve> monthQuests = questAchieveService.getMonth(member);
            return ResponseEntity.ok(monthQuests);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
