package org.hackathon.economy.quiz.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "daily_quiz")
public class DailyQuiz {
    @Id
    @GeneratedValue
    @Column(name="daily_quiz_id")
    private Long dailyQuizId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_pk", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, name="quiz_date_time")
//    private LocalDate quizDate;
    private LocalDateTime quizDateTime;

}
