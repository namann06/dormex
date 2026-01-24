package com.dormex.service;

import com.dormex.dto.menu.*;
import com.dormex.entity.MessMenu;
import com.dormex.entity.enums.DayOfWeek;
import com.dormex.entity.enums.MealType;
import com.dormex.exception.BadRequestException;
import com.dormex.exception.ResourceNotFoundException;
import com.dormex.repository.MessMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessMenuService {

    private final MessMenuRepository menuRepository;

    @Transactional
    public MenuResponse createMenu(CreateMenuRequest request) {
        // Check if menu already exists for this day and meal
        if (menuRepository.findByDayOfWeekAndMealType(request.getDayOfWeek(), request.getMealType()).isPresent()) {
            throw new BadRequestException("Menu already exists for " + request.getDayOfWeek() + " " + request.getMealType());
        }

        MessMenu menu = MessMenu.builder()
                .dayOfWeek(request.getDayOfWeek())
                .mealType(request.getMealType())
                .items(request.getItems())
                .specialNote(request.getSpecialNote())
                .build();

        return MenuResponse.fromEntity(menuRepository.save(menu));
    }

    @Transactional
    public MenuResponse updateMenu(Long id, UpdateMenuRequest request) {
        MessMenu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        if (request.getItems() != null && !request.getItems().isBlank()) {
            menu.setItems(request.getItems());
        }
        if (request.getSpecialNote() != null) {
            menu.setSpecialNote(request.getSpecialNote());
        }

        return MenuResponse.fromEntity(menuRepository.save(menu));
    }

    @Transactional
    public MenuResponse updateMenuByDayAndMeal(DayOfWeek day, MealType meal, UpdateMenuRequest request) {
        MessMenu menu = menuRepository.findByDayOfWeekAndMealType(day, meal)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found for " + day + " " + meal));

        if (request.getItems() != null && !request.getItems().isBlank()) {
            menu.setItems(request.getItems());
        }
        if (request.getSpecialNote() != null) {
            menu.setSpecialNote(request.getSpecialNote());
        }

        return MenuResponse.fromEntity(menuRepository.save(menu));
    }

    public MenuResponse getMenuById(Long id) {
        return menuRepository.findById(id)
                .map(MenuResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));
    }

    public List<MenuResponse> getMenuByDay(DayOfWeek day) {
        return menuRepository.findByDayOfWeekOrderByMealType(day).stream()
                .map(MenuResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public MenuResponse getMenuByDayAndMeal(DayOfWeek day, MealType meal) {
        return menuRepository.findByDayOfWeekAndMealType(day, meal)
                .map(MenuResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found for " + day + " " + meal));
    }

    public List<DailyMenuResponse> getWeeklyMenu() {
        List<MessMenu> allMenus = menuRepository.findAllByOrderByDayOfWeekAscMealTypeAsc();

        Map<DayOfWeek, List<MessMenu>> groupedByDay = allMenus.stream()
                .collect(Collectors.groupingBy(MessMenu::getDayOfWeek));

        List<DailyMenuResponse> weeklyMenu = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            List<MenuResponse> meals = groupedByDay.getOrDefault(day, List.of()).stream()
                    .map(MenuResponse::fromEntity)
                    .collect(Collectors.toList());

            weeklyMenu.add(DailyMenuResponse.builder()
                    .day(day)
                    .meals(meals)
                    .build());
        }

        return weeklyMenu;
    }

    public DailyMenuResponse getTodayMenu() {
        java.time.DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();
        DayOfWeek day = DayOfWeek.valueOf(today.name());

        List<MenuResponse> meals = getMenuByDay(day);
        return DailyMenuResponse.builder()
                .day(day)
                .meals(meals)
                .build();
    }

    @Transactional
    public void deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu not found");
        }
        menuRepository.deleteById(id);
    }
}
