package com.dormex.dto.menu;

import com.dormex.entity.enums.DayOfWeek;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DailyMenuResponse {

    private DayOfWeek day;
    private List<MenuResponse> meals;
}
