package com.recruiment.batchnoti.batchs.steps.ads;

import com.recruiment.batchnoti.records.CampaignStat;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.text.NumberFormat;
import java.util.Locale;

@Configuration
public class AdsStatsStep {

    @Bean
    public Step exportStatsStep(JobRepository jobRepository,
                                JdbcCursorItemReader<CampaignStat> statsReader,
                                FlatFileItemWriter<CampaignStat> statsWriter) {
        return new StepBuilder("exportStatsStep", jobRepository)
            .<CampaignStat, CampaignStat>chunk(1000)
            .reader(statsReader)
            .writer(statsWriter)
            .build();
    }

    // 1. Reader: Đọc dữ liệu đã thống kê từ PostgreSQL
    @Bean
    public JdbcCursorItemReader<CampaignStat> statsReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<CampaignStat>()
            .name("statsReader")
            .dataSource(dataSource)
            .sql("SELECT campaign_id, SUM(impressions) as total_impressions, " +
                 "SUM(clicks) as total_clicks, SUM(spend)::numeric as total_spend " +
                 "FROM staging_ads GROUP BY campaign_id")
            .rowMapper((rs, rowNum) ->
                     new CampaignStat(
                        rs.getString("campaign_id"),
                        rs.getLong("total_impressions"),
                        rs.getLong("total_clicks"),
                        rs.getBigDecimal("total_spend")
            ))
            .build();
    }

    // 2. Writer: Ghi ra file CSV
    @Bean
    public FlatFileItemWriter<CampaignStat> statsWriter() {
        return new FlatFileItemWriterBuilder<CampaignStat>()
            .name("statsWriter")
            .resource(new FileSystemResource("reports/campaign_stats_report.csv"))
            .delimited()
            .delimiter(",")
            .names("campaignId", "totalImpressions", "totalClicks", "totalSpend")
            .headerCallback(writer -> writer.write("campaign_id,total_impressions,total_clicks,total_spend"))
            .build();
    }
}
