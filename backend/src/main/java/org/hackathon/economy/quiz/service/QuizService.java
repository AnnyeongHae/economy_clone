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
        Long questNo = 1L; // 일일 퀘스트
        Integer result = questAchieveRepository.countRecentAchievement(memberNo, questNo);
        return result != null && result > 0;
    }

    // 퀴즈 제출 및 로그 기록
    @Transactional
    public boolean submitQuizAnswer(Long memberNo, Long quizPk, Integer userAnswer) {
        Quiz quiz = quizRepository.findById(quizPk).orElseThrow(() -> new RuntimeException("Quiz not found."));
        boolean isCorrect = quiz.getQuizAnswer().equals(userAnswer);

        // 퀴즈 제출 로그 기록
        QuizSubmissionLog log = new QuizSubmissionLog();
        log.setMemberNo(memberNo);
        log.setQuiz(quiz);
        log.setUserAnswer(userAnswer);
        log.setIsCorrect(isCorrect);
        log.setSubmissionTime(new java.sql.Timestamp(System.currentTimeMillis()));
        submissionLogRepository.save(log);

        // 정답일 경우 QuestAchieve 업데이트
        if (isCorrect) {
            Long questNo = 1L; // 일일 퀘스트
            boolean alreadyAchieved = hasCompletedDailyQuiz(memberNo);
            if (!alreadyAchieved) {
                QuestAchieve achieve = new QuestAchieve();

                // Member 설정
                Member member = new Member();
                member.setMemberNo(memberNo);
                achieve.setMember(member);

                // Quest 설정
                Quest quest = new Quest();
                quest.setQuestNo(questNo);
                achieve.setQuest(quest);

                achieve.setAchieveDateTime(new java.sql.Timestamp(System.currentTimeMillis()));
                questAchieveRepository.save(achieve);
                System.out.println("QuestAchieve updated: Member " + memberNo + ", Quest " + questNo);
            }
        }
        return isCorrect;
    }
}
