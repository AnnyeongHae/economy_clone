package org.hackathon.economy.batch.tasklet;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.hackathon.economy.quest.repository.QuestRepositoryInterface;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Component("monthlyQuestTasklet")
public class MonthlyQuestTasklet implements Tasklet {
    private final QuestRepositoryInterface questRepositoryInterface;

    @Autowired
    public MonthlyQuestTasklet(QuestRepositoryInterface questRepositoryInterface) {
        this.questRepositoryInterface = questRepositoryInterface;
    }

    @Override
    @Transactional
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        return RepeatStatus.FINISHED; // 모든 작업이 완료되면 RepeatStatus.FINISHED를 반환
    }
}
