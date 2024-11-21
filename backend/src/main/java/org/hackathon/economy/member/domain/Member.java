package org.hackathon.economy.member.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hackathon.economy.account.domain.Account;
import org.hackathon.economy.greenttransaction.domain.GreenTransaction;
import org.hackathon.economy.quest.domain.Quest;
import org.hackathon.economy.quest.domain.QuestAchieve;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "member")
public class Member {

    @Id @GeneratedValue
    private Long memberNo;
    @Column(nullable = false)
    private String memberName;
    @ColumnDefault("0")
    private Integer memberGrade;
    @ColumnDefault("0")
    private Long memberPoint;
    @Column(nullable = false)
    private String memberEmail;
    @Column(nullable = false)
    private String memberPassword;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Account account;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestAchieve> questAchieves = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createDate = new Date(); // 현재 시간 자동 설정
        this.updatedDate = new Date();
    }
}
