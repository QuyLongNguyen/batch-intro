package com.recruiment.batchnoti;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchNotiApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(BatchNotiApplication.class, args);
    }

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job importAdsJob;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JobParameters parameters = new JobParametersBuilder()
            .toJobParameters();
        jobOperator.start(importAdsJob, parameters);
    }
}
