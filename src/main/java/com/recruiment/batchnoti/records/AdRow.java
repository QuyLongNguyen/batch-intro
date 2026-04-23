package com.recruiment.batchnoti.records;

/**
 * @author longnguyen on 22/4/26
 * @product IntelliJ IDEA
 * @project batch-noti
 *
 */

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AdRow(String campaignId,
                    @DateTimeFormat(pattern = "yyyy-MM-dd")
                    LocalDate date,
                    long impressions,
                    long clicks,
                    BigDecimal spend,
                    long conversions)
    implements Serializable {
}
