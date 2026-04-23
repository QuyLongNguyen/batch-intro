package com.recruiment.batchnoti.batchs.steps.ads;

import com.recruiment.batchnoti.records.AdRow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.apache.commons.lang3.math.NumberUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

/**
 * @author longnguyen on 22/4/26
 * @product IntelliJ IDEA
 * @project batch-noti
 *
 */
@Configuration
public class AdsCsvStep {

    @Value("${batch.chunk.size}")
    private int chunkSize;

    @Bean
    public Step importAdsStep(JobRepository jobRepository,
                           ItemReader<AdRow> reader,
                           ItemWriter<AdRow> writer) {
        return new StepBuilder("importAdsStep", jobRepository)
            .<AdRow, AdRow>chunk(chunkSize)
            .reader(reader)
            .writer(writer)
            .build();

    }

    @Bean
    public FlatFileItemReader<AdRow> csvItemReader() {
        return new FlatFileItemReaderBuilder<AdRow>()
            .name("adsItemReader")
            .resource(new ClassPathResource("ad_data.csv"))
            .delimited()
            .names("campaign_id", "date", "impressions" , "clicks", "spend", "conversions")
            .fieldSetMapper(this::mapFieldSet)
            .linesToSkip(1)
            .build();
    }

    @Bean
    public JdbcBatchItemWriter<AdRow> csvItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AdRow>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("insert into staging_ads (campaign_id, dob, impressions, clicks, spend, conversions) " +
                "values (:campaignId, :date, :impressions, :clicks, :spend, :conversions)")
            .dataSource(dataSource)
            .build();
    }

    public AdRow mapFieldSet(FieldSet fs) {
        if (fs == null) {
            return null;
        }
        //campaign_id,date,impressions,clicks,spend,conversions
        var campaignId = fs.readString("campaign_id");
        var date = LocalDate.MIN;
        try{
            date = LocalDate.parse(fs.readString("date"), DateTimeFormatter.ISO_LOCAL_DATE);
        }catch (DateTimeParseException e){
            System.out.printf(fs.readString("date"));
        }

        var impressions = toLong(fs.readString("date"), 0);
        var clicks = toLong(fs.readString("clicks"), 0);
        var spend =  isDigits(fs.readString("spend"))
            ? createBigDecimal(fs.readString("spend"))
            : BigDecimal.ZERO;
        var conversions = toLong(fs.readString("conversions"), 0);

        return new AdRow(campaignId, date, impressions, clicks, spend, conversions);
    }

}
