package org.hackathon.economy.quiz.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyQuizScheduler {

    private final DailyQuizService dailyQuizService;

    public DailyQuizScheduler(DailyQuizService dailyQuizService) {
        this.dailyQuizService = dailyQuizService;
    }

    // 30초마다 새로운 퀴즈 업데이트
    //    @Scheduled(cron = "0/30 * * * * *")
    public void updateDailyQuizTask() {
        dailyQuizService.updateDailyQuiz();
        System.out.println("Running scheduled task: Updating daily quiz");
    }
}