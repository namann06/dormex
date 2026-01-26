package com.dormex.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ComplaintSummary {

    private long total;
    private Map<String, Long> byStatus;
    private Map<String, Long> byCategory;
}
