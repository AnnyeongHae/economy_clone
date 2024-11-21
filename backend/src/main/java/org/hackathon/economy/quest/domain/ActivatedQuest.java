package org.hackathon.economy.quest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "ACTIVATED_QUEST")
public class ActivatedQuest {

    @Id
    @GeneratedValue
    private Long activatedQuestNo;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_no") // 외래 키를 명시적으로 설정
    private Quest quest;
}
