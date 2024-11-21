package org.hackathon.economy.quiz.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hackathon.economy.quest.domain.Quest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="quiz_pk")
    private Long quizPk;

    @Column(nullable = false, name="quiz_title")
    private String quizTitle;

    @Column(nullable = false, name="quiz_desc")
    private String quizDesc;

    @Column(nullable = false, name="quiz_answer")
    private Integer quizAnswer;

    @Column(nullable = false, name="quiz_type")
    private String quizType;

    @Column(nullable = false, name="quiz_cumulated")
    private Integer quizCumulated = 0;

    @Column(nullable = false, name="quiz_created_at")
    private Timestamp quizCreatedAt;

    @Column(nullable = false, name="quiz_updated_at")
    private Timestamp quizUpdatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_no")
    @JsonIgnore
    private Quest quest;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizOption> options = new ArrayList<>();
}