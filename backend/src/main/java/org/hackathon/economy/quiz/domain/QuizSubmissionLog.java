package org.hackathon.economy.quiz.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "quiz_submission_log")
public class QuizSubmissionLog {

    @Id
    @GeneratedValue
    @Column(name= "log_id", nullable = false)
    private Long logId;

    @Column(name="member_no", nullable = false)
    private Long memberNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_pk", nullable = false)
    private Quiz quiz;

    @Column(name="is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name="user_answer", nullable = true)
    private Integer userAnswer;

    @Column(name="submission_time", nullable = false)
    private Timestamp submissionTime;
}