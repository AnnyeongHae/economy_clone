package org.hackathon.economy.qr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "qr_achieve")
public class QrAchieve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qrAchieveNo;

    @Column(name= "achieve_date_time", nullable = false)
    private LocalDateTime achieveDateTime = LocalDateTime.now();

    @Column(name= "member_no", nullable = false)
    private Long memberNo;

    @Column(name= "quest_no", nullable = false)
    private Long questNo;

    @Column(name= "session_id", nullable = false)
    private Long sessionId;

    @Column(name= "creator_member_no", nullable = false)
    private Long creatorMemberNo;

}
