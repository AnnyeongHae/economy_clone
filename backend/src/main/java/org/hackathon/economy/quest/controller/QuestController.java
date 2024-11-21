package org.hackathon.economy.quest.controller;

import lombok.RequiredArgsConstructor;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.service.QuestAuthService;
import org.hackathon.economy.quest.service.QuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quest")
public class QuestController {

    private final QuestService questService;
    private final QuestAuthService questAuthService;

    // 진행 중인 퀘스트 전체 조회
    @GetMapping("/active")
    public ResponseEntity<List<Quest>> getActiveQuests() {
        return ResponseEntity.ok(questService.getActiveQuests());
    }

    // 진행 중인 일일 퀘스트 조회
    @GetMapping("/active/daily")
    public ResponseEntity<List<Quest>> getActiveDailyQuests() {
        return ResponseEntity.ok(questService.getActiveDailyQuests());
    }

    // 진행 중인 주간 퀘스트 조회
    @GetMapping("/active/weekly")
    public ResponseEntity<List<Quest>> getActiveWeelyQuests() {
        return ResponseEntity.ok(questService.getActiveWeeklyQuests());
    }


    // 진행 중인 월간 퀘스트 조회
    @GetMapping("/active/monthly")
    public ResponseEntity<List<Quest>> getActiveMonthlyQuests() {
        return ResponseEntity.ok(questService.getActiveMonthlyQuests());
    }
  
   @PostMapping("/process")
    public ResponseEntity<String> processQuestAchievements(@RequestBody Long memberNo) {
        try {
            questAuthService.processQuestAchievements(memberNo);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("error" + e.getMessage());
        }
    }

    @GetMapping("/recommend")
    public ResponseEntity<Quest> getRecommendQuest() {
        return ResponseEntity.ok(questService.getRecommendQuest());
    }
}
