package com.engeto.restaurant.model;

import com.engeto.restaurant.util.RestaurantException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CookBook {

    private static Map<String, Dish> dishMap = new LinkedHashMap<>();

    public static void addDishToCookBook(Dish dish) {
        dishMap.put(dish.getTitle(), dish);
    }

    public static void removeDishByIdFromCookBook(long dishId) throws RestaurantException {
        try {
            Dish dishToRemove = getDishByIdFromCookBook(dishId);
            dishMap.remove(dishToRemove.getTitle());
            System.out.println("Pokrm č. " + dishId + " - " + dishToRemove.getTitle() + " byl úspěšně odstraněn.");
        } catch (RestaurantException e) {
            throw new RestaurantException("Chyba při pokusu o odstranění pokrmu: " + e.getMessage());
        }
    }

    public static Dish getDishByIdFromCookBook(long dishId) throws RestaurantException {
        for (Dish dish : dishMap.values()) {
            if (dish.getId() == dishId) {
                return dish;
            }
        }
        throw new RestaurantException("Pokrm s identifikačním číslem [" + dishId + "] nenalezen.");
    }

    public static Map<String, Dish> getAllDishes() throws RestaurantException {
        if (!dishMap.isEmpty()) {
            return dishMap;
        } else {
            throw new RestaurantException("Seznam jídel je prázdný.");
        }
    }

    public static String getAllDishesAsString() {
        StringBuilder result = new StringBuilder("Seznam jídel");
        if (!dishMap.isEmpty()) {
            result.append(":\n");

            for (Map.Entry<String, Dish> entry : dishMap.entrySet()) {
                Dish dish = entry.getValue();
                result.append(String.format("%s - %s Kč, příprava: %s minut, obrázek: %s\n",
                        dish.getTitle(), dish.getPrice(), dish.getPreparationTimeInMinutes(), dish.getImage()));
            }
        } else {
            result.append(" je prázdný.");
        }
        return result.toString();
    }
}