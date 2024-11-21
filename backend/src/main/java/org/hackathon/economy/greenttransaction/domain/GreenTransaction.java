package org.hackathon.economy.greenttransaction.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hackathon.economy.member.domain.Member;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "green_transactions")
public class GreenTransaction {
    @Id
    @GeneratedValue
    private Long transactionNo;
    private LocalDateTime transactionTime;
    private String serviceProvider;
    private BigDecimal transactionAmount;
    private String transactionCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;
}