package com.recruiment.batchnoti.records;

import java.math.BigDecimal;

public record CampaignStat(
    String campaignId,
    long totalImpressions,
    long totalClicks,
    BigDecimal totalSpend
) {}
