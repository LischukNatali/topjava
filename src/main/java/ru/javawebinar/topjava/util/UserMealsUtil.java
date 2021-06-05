package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    /**
     * Реализовать метод `UserMealsUtil.filteredByCycles` через циклы (`forEach`):
     * -  должны возвращаться только записи между `startTime` и `endTime`
     * -  поле `UserMealWithExcess.excess` должно показывать,
     *                                      превышает ли сумма калорий за весь день значение `caloriesPerDay`
     *
     * Т.е `UserMealWithExcess` - это запись одной еды, но поле `excess` будет одинаково для всех записей за этот день.
     *
     * - Проверьте результат выполнения ДЗ (можно проверить логику в http://topjava.herokuapp.com , список еды)
     * - Оцените Time complexity алгоритма. Если она больше O(N), например O(N*N) или N*log(N), сделайте O(N).
     * @param meals
     * @param startTime
     * @param endTime
     * @param caloriesPerDay
     * @return
     */
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumPerDay = new HashMap();
        for (UserMeal meal: meals) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            caloriesSumPerDay.put(mealDate, caloriesSumPerDay.getOrDefault(mealDate, 0) + meal.getCalories());
        }

        List<UserMealWithExcess> usersMealWithExcesses = new ArrayList<>();
        for (UserMeal meal: meals) {
            LocalDateTime dateTime = meal.getDateTime();

            if (TimeUtil.isBetweenHalfOpen(dateTime.toLocalTime(), startTime, endTime)) {

                usersMealWithExcesses.add(new UserMealWithExcess(dateTime, meal.getDescription(), meal.getCalories(),
                        caloriesSumPerDay.get(dateTime.toLocalDate()) > caloriesPerDay));

            }
        }
        return usersMealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumPerDay = meals
                .stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));


        return meals
                .stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesSumPerDay.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
