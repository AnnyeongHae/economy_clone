package org.hackathon.economy.quiz.service;

import org.hackathon.economy.member.repository.MemberRepository;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.domain.QuestAchieve;
import org.hackathon.economy.quest.repository.QuestAchieveRepository;
import org.hackathon.economy.quiz.domain.Quiz;
import org.hackathon.economy.member.domain.Member;
import org.hackathon.economy.quiz.domain.QuizOption;
import org.hackathon.economy.quiz.domain.QuizSubmissionLog;
import org.hackathon.economy.quiz.repository.QuizOptionRepository;
import org.hackathon.economy.quiz.repository.QuizRepository;
import org.hackathon.economy.quiz.repository.QuizSubmissionLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {
    //    private static final Long DEFAULT_MEMBER_NO = 1L;
    private final QuizRepository quizRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final QuizSubmissionLogRepository submissionLogRepository;
    private final QuestAchieveRepository questAchieveRepository;
    private final DailyQuizService dailyQuizService;
    public QuizService(QuizRepository quizRepository, QuizOptionRepository quizOptionRepository,
                       QuizSubmissionLogRepository submissionLogRepository,
                       QuestAchieveRepository questAchieveRepository,
                       DailyQuizService dailyQuizService) {
        this.quizRepository = quizRepository;
        this.quizOptionRepository = quizOptionRepository;
        this.submissionLogRepository = submissionLogRepository;
        this.questAchieveRepository = questAchieveRepository;
        this.dailyQuizService = dailyQuizService;
    }
    // 오늘의 퀴즈 가져오기
    @Transactional
    public Quiz getDailyQuiz() {
        return dailyQuizService.getTodayQuiz();
    }
    // 퀴즈 옵션 가져오기
    @Transactional
    public List<QuizOption> getQuizOptions(Long quizPk) {
        Quiz quiz = quizRepository.findById(quizPk)
                .orElseThrow(() -> new RuntimeException("Quiz not found."));
        List<QuizOption> options = quizOptionRepository.findQuizOptionsByQuiz(quiz);
        options.size(); // LazyInitialization 방지
        return options;
    }
    // 사용자가 오늘의 퀴즈를 완료했는지 확인
    @Transactional
    public boolean hasCompletedDailyQuiz(Long memberNo) {
        // 오늘 날짜 계산
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        // quiz_submission_log에서 오늘 날짜와 정답 여부 확인
        long count = submissionLogRepository.countByMemberNoAndIsCorrectAndSubmissionTimeBetween(
                memberNo, true, Timestamp.valueOf(todayStart), Timestamp.valueOf(todayEnd)
        );
        return count > 0; // 오늘 정답 제출 기록이 있으면 완료
    }
    // 퀴즈 제출 및 로그 기록
    @Transactional
    public boolean submitQuizAnswer(Long memberNo, Long quizPk, Integer userAnswer) {
        Quiz quiz = quizRepository.findById(quizPk)
                .orElseThrow(() -> new RuntimeException("Quiz not found."));
        boolean isCorrect = quiz.getQuizAnswer().equals(userAnswer);
        // 퀴즈 제출 로그 기록
        QuizSubmissionLog log = new QuizSubmissionLog();
        log.setMemberNo(memberNo);
        log.setQuiz(quiz);
        log.setUserAnswer(userAnswer);
        log.setIsCorrect(isCorrect);
        log.setSubmissionTime(new Timestamp(System.currentTimeMillis()));
        submissionLogRepository.save(log);
        return isCorrect;
    }
}



