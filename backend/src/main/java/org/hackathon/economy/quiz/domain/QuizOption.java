package org.hackathon.economy.quiz.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "quiz_option")
public class QuizOption {

    @Id
    @GeneratedValue
    @Column(name = "option_pk") // 매핑 이름 명시
    private Long optionPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_pk", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    @Column(nullable = false, name = "option_id") // 정확한 컬럼 이름 지정
    private Long optionId;

    @Column(nullable = false, name = "option_text")
    private String optionText;

    @Column(nullable = false, name = "is_correct")
    private Boolean isCorrect;
}

