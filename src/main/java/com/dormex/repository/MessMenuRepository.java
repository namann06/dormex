package com.dormex.repository;

import com.dormex.entity.MessMenu;
import com.dormex.entity.enums.DayOfWeek;
import com.dormex.entity.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessMenuRepository extends JpaRepository<MessMenu, Long> {

    List<MessMenu> findByDayOfWeekOrderByMealType(DayOfWeek dayOfWeek);

    Optional<MessMenu> findByDayOfWeekAndMealType(DayOfWeek dayOfWeek, MealType mealType);

    List<MessMenu> findByMealTypeOrderByDayOfWeek(MealType mealType);

    List<MessMenu> findAllByOrderByDayOfWeekAscMealTypeAsc();
}
