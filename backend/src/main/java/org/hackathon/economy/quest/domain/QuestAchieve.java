package org.hackathon.economy.quest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hackathon.economy.member.domain.Member;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "QUEST_ACHIEVE")
public class QuestAchieve {

    @Id
    //이 문제는 MySQL에서 JPA가 시퀀스(Sequence) 를 사용하려고 할 때 발생할 수 있습니다. MySQL은 시퀀스를 지원하지 않기 때문에, 기본적으로 JPA에서는 테이블 생성 전략을 사용하게 됩니다.
    // Spring Data JPA에서 @GeneratedValue 어노테이션을 사용할 때, 기본적으로 GenerationType.AUTO 를 사용합니다. 이 경우, 데이터베이스가 시퀀스 전략을 사용할 수 있다고 판단하고, 시퀀스를 찾게 됩니다. MySQL은 시퀀스를 지원하지 않으므로, 대신 GenerationType.IDENTITY 또는 GenerationType.TABLE 을 사용해야 합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 수정된 부분
    private Long achieveNo;

    @Column(nullable = false)
    private Timestamp achieveDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quest_no") // 외래 키를 명시적으로 설정
    private Quest quest;
}
