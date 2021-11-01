package com.xxl.job.executor.service.jobhandler.job;

import com.xxl.job.core.context.XxlJobHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class DemoBatchJobConfig {
    public JobBuilderFactory jobBuilderFactory;
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job demoSpringBatchJob() {
        return jobBuilderFactory.get("demoSpringBatchJob")
                .start(handleDataStep())
                .build();
    }

    @Bean
    public Step handleDataStep() {
        return stepBuilderFactory.get("getData").tasklet(
                (stepContribution, chunkContext) -> {
                    log.error("SpringBatch, Hello World.");
                    XxlJobHelper.log("SpringBatch, Hello World.");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
