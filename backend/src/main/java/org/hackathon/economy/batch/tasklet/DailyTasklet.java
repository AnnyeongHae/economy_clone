package org.hackathon.economy.batch.tasklet;

import org.hackathon.economy.account.repository.DailyInterestRepositoryInterface;
import org.hackathon.economy.account.repository.InterestRepositoryInterface;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("dailyTasklet")
public class DailyTasklet implements Tasklet {

    private final InterestRepositoryInterface interestRepositoryInterface;
    private final DailyInterestRepositoryInterface dailyInterestRepositoryInterface;

    @Autowired
    public DailyTasklet(InterestRepositoryInterface interestRepositoryInterface, DailyInterestRepositoryInterface dailyInterestRepositoryInterface) {
        this.interestRepositoryInterface = interestRepositoryInterface;
        this.dailyInterestRepositoryInterface = dailyInterestRepositoryInterface;
    }

    @Override
    @Transactional // 전체 메서드를 하나의 트랜잭션으로 묶음
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        interestRepositoryInterface.findAll().forEach(interest -> {

            // 데일리 퀘스트 달성 확인
            interest.setCurrentDaily(interest.getDailyQuest());
            interest.setDailyQuest(false);
            interestRepositoryInterface.save(interest);

//            if (interest.getDailyQuest()) { // 데일리 퀘스트 달성 확인
//                interest.setCurrentDaily(true);
//                interest.setDailyQuest(false);
//                interestRepositoryInterface.save(interest);
//            }

        });

        return RepeatStatus.FINISHED; // 모든 작업이 완료되면 RepeatStatus.FINISHED를 반환
    }
}
