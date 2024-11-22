package org.hackathon.economy.quest.domain;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class AchieveDTO {
    private Long achieveNo;
    private Timestamp achieveDateTime;
}
