package org.hackathon.economy.quest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hackathon.economy.member.domain.Member;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "QUEST")
public class Quest {

    @Id
    @GeneratedValue
    private Long questNo;
    @Column(nullable = false)
    private Integer questType;
    @Column(nullable = false)
    private String questContent;
    @Column(nullable = false)
    private Integer questPoint;
    @Column(nullable = false)
    private Integer questCount;
    @Column(nullable = false)
    private Date createTableDatetime;

    @OneToOne(fetch = FetchType.LAZY)
    private QuestAchieve questAchieve;
}
