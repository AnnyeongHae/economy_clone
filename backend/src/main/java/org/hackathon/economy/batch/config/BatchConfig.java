package org.hackathon.economy.batch.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Log4j2
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    // Daily Job
    @Bean
    public Job dailyUpdateInterestJob(Step dailyUpdateInterestStep) {
        JobBuilder jobBuilder = new JobBuilder("dailyUpdateInterestJob", jobRepository);
        return jobBuilder.incrementer(new RunIdIncrementer())
                .start(dailyUpdateInterestStep)
                .build();
    }
    @Bean
    public Job dailyInsertInterestJob(Step dailyInsertInterestStep) {
        JobBuilder jobBuilder = new JobBuilder("dailyInsertInterestJob", jobRepository);
        return jobBuilder.incrementer(new RunIdIncrementer())
                .start(dailyInsertInterestStep)
                .build();
    }

    // Weekly Job
    @Bean
    public Job weeklyUpdateInterestJob(Step weeklyUpdateInterestStep) {
        JobBuilder jobBuilder = new JobBuilder("weeklyUpdateInterestJob", jobRepository);
        return jobBuilder.incrementer(new RunIdIncrementer())
                .start(weeklyUpdateInterestStep)
                .build();
    }
    @Bean
    public Job weeklyUpdateQuestJob(Step weeklyUpdateQuestStep) {
        JobBuilder jobBuilder = new JobBuilder("weeklyUpdateInterestJob", jobRepository);
        return jobBuilder.incrementer(new RunIdIncrementer())
                .start(weeklyUpdateQuestStep)
                .build();
    }


    // Monthly Job
    @Bean
    public Job monthlyUpdateInterestJob(Step monthlyUpdateInterestStep) {
        JobBuilder jobBuilder = new JobBuilder("monthlyUpdateInterestJob", jobRepository);
        return jobBuilder.incrementer(new RunIdIncrementer())
                .start(monthlyUpdateInterestStep)
                .build();
    }
    @Bean
    public Job monthlyUpdateQuestJob(Step monthlyUpdateQuestStep) {
        JobBuilder jobBuilder = new JobBuilder("monthlyUpdateQuestJob", jobRepository);
        return jobBuilder.incrementer(new RunIdIncrementer())
                .start(monthlyUpdateQuestStep)
                .build();
    }


    // Daily Step
    @Bean
    public Step dailyUpdateInterestStep(@Qualifier("dailyTasklet") Tasklet dailyUpdateInterestTasklet) {
        StepBuilder stepBuilder = new StepBuilder("dailyUpdateInterestStep", jobRepository);
        return stepBuilder.tasklet(dailyUpdateInterestTasklet, transactionManager).build();
    }
    @Bean
    public Step dailyInsertInterestStep(@Qualifier("dailyInterestTasklet") Tasklet dailyInsertInterestTasklet) {
        StepBuilder stepBuilder = new StepBuilder("dailyInsertInterestStep", jobRepository);
        return stepBuilder.tasklet(dailyInsertInterestTasklet, transactionManager).build();
    }


    // Weekly Step
    @Bean
    public Step weeklyUpdateInterestStep(@Qualifier("weeklyTasklet") Tasklet weeklyUpdateInterestTasklet) {
        StepBuilder stepBuilder = new StepBuilder("weeklyUpdateInterestStep", jobRepository);
        return stepBuilder.tasklet(weeklyUpdateInterestTasklet, transactionManager).build();
    }
    @Bean
    public Step weeklyUpdateQuestStep(@Qualifier("weeklyQuestTasklet") Tasklet weeklyUpdateQuestTasklet) {
        StepBuilder stepBuilder = new StepBuilder("weeklyUpdateQuestStep", jobRepository);
        return stepBuilder.tasklet(weeklyUpdateQuestTasklet, transactionManager).build();
    }


    // Monthly Step
    @Bean
    public Step monthlyUpdateInterestStep(@Qualifier("monthlyTasklet") Tasklet monthlyUpdateInterestTasklet) {
        StepBuilder stepBuilder = new StepBuilder("monthlyUpdateInterestStep", jobRepository);
        return stepBuilder.tasklet(monthlyUpdateInterestTasklet, transactionManager).build();
    }
    @Bean
    public Step monthlyUpdateQuestStep(@Qualifier("monthlyQuestTasklet") Tasklet monthlyUpdateQuestTasklet) {
        StepBuilder stepBuilder = new StepBuilder("monthlyUpdateQuestStep", jobRepository);
        return stepBuilder.tasklet(monthlyUpdateQuestTasklet, transactionManager).build();
    }

//    @Bean
//    public Step updateUserStep(Tasklet userTasklet) {
//        StepBuilder stepBuilder = new StepBuilder("updateUserStep", jobRepository);
//        TaskletStep step = stepBuilder.tasklet(userTasklet, transactionManager).build();
//        return step;
//    }
}
