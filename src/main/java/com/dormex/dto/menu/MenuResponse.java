package com.dormex.dto.menu;

import com.dormex.entity.MessMenu;
import com.dormex.entity.enums.DayOfWeek;
import com.dormex.entity.enums.MealType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class MenuResponse {

    private Long id;
    private DayOfWeek dayOfWeek;
    private MealType mealType;
    private String items;
    private List<String> itemList;
    private String specialNote;
    private LocalDateTime updatedAt;

    public static MenuResponse fromEntity(MessMenu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .dayOfWeek(menu.getDayOfWeek())
                .mealType(menu.getMealType())
                .items(menu.getItems())
                .itemList(Arrays.asList(menu.getItems().split("\\s*,\\s*")))
                .specialNote(menu.getSpecialNote())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}
