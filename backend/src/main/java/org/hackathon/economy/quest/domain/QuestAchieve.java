package org.hackathon.economy.quest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hackathon.economy.member.domain.Member;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "quest_achieve")
public class QuestAchieve {

    @Id
    @GeneratedValue// 수정된 부분
    private Long achieveNo;

    @Column(nullable = false)
    private Timestamp achieveDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_no") // 외래 키를 명시적으로 설정
    private Quest quest;
}


