package com.recruiment.batchnoti.batchs.jobs.ads;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author longnguyen on 23/4/26
 * @product IntelliJ IDEA
 * @project batch-noti
 *
 */
@Configuration
@RequiredArgsConstructor
public class AdsCsvJob {

    private final Step importAdsStep;
    private final Step exportStatsStep;

    @Bean
    public Job importAdsJob(JobRepository jobRepository){
        return new JobBuilder("importAdsJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(importAdsStep)
            .next(exportStatsStep)
            .build();
    }
}
