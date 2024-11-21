package org.hackathon.economy.quiz.controller;

import org.hackathon.economy.quiz.domain.Quiz;
import org.hackathon.economy.quiz.domain.QuizOption;
import org.hackathon.economy.quiz.domain.QuizSubmissionDto;
import org.hackathon.economy.quiz.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
//@CrossOrigin(origins = "http://localhost:5173") // Vue 개발 서버 주소
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/daily")
    public ResponseEntity<Quiz> getDailyQuiz() {
        Quiz quiz = quizService.getDailyQuiz();
        return ResponseEntity.ok(quiz);
    }
    @GetMapping("/options/{quizPk}")
    public ResponseEntity<List<QuizOption>> getQuizOptions(@PathVariable Long quizPk) {
        return ResponseEntity.ok(quizService.getQuizOptions(quizPk));
    }

    @GetMapping("/check-completion")
    public ResponseEntity<Boolean> checkDailyQuizCompletion(@RequestParam(value = "memberNo", defaultValue="1") Long memberNo) {
        boolean hasCompleted = quizService.hasCompletedDailyQuiz(memberNo);
        return ResponseEntity.ok(hasCompleted);
    }

    @PostMapping("/submit")
    public ResponseEntity<Boolean> submitQuizAnswer(@RequestParam(value = "memberNo", defaultValue="1") Long memberNo, @RequestBody QuizSubmissionDto submissionDto) {
//                                                      추후에 memberNo나오면 submissionDto.getMemberNo(),
        boolean isCorrect = quizService.submitQuizAnswer(memberNo, submissionDto.getQuizPk(), submissionDto.getUserAnswer());
        return ResponseEntity.ok(isCorrect);
    }
}
