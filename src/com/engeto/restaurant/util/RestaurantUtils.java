package com.engeto.restaurant.util;

import com.engeto.restaurant.model.CookBook;
import com.engeto.restaurant.model.Dish;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;


public class RestaurantUtils {
    public static void validatePrice(BigDecimal price) throws RestaurantException {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RestaurantException("Cena pokrmu nesmí být menší než nula. Zadáno: " + price);
        }
    }

    public static void validatePreparationTime(int preparationTime) throws RestaurantException {
        if (preparationTime <= 0) {
            throw new RestaurantException("Čas přípravy pokrmu musí být větší než nula. Zadáno: " + preparationTime + " minut.");
        }
    }

    public static void validateTime(LocalDateTime time, String fieldName) throws RestaurantException {
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (time.isAfter(currentDateTime)) {
            throw new RestaurantException(fieldName + " nemůže být v budoucnosti. Zadáno: " + time);
        }
    }

    public static BigDecimal parsePrice(String priceString) throws RestaurantException {
        try {
            BigDecimal price = new BigDecimal(priceString.trim());
            RestaurantUtils.validatePrice(price);
            return price;
        } catch (RestaurantException e) {
            throw new RestaurantException("Chyba při zpracování ceny jídla: "+ e.getMessage());
        } catch (NumberFormatException e) {
            throw new RestaurantException("Chyba při zpracování ceny jídla: Neplatné číslo (" + e.getMessage() + ")");
        }
    }

    public static int parsePreparationTime(String prepTimeString) throws RestaurantException {
        try {
            int preparationTime = Integer.parseInt(prepTimeString.trim());
            RestaurantUtils.validatePreparationTime(preparationTime);
            return preparationTime;
        } catch (RestaurantException e) {
            throw new RestaurantException("Chyba při zpracování doby přípravy jídla: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new RestaurantException("Chyba při zpracování doby přípravy jídla: Neplatné číslo (" + e.getMessage() + ")");
        }
    }

    public static void validateNumberOfBlocks(String[] parts, int expectedNumberOfBlocks) throws RestaurantException {
        int numOfBlocks = parts.length;
        if (numOfBlocks != expectedNumberOfBlocks) {
            throw new RestaurantException(
                    String.format("Nesprávný počet položek na řádku! Očekáváný: %d, skutečný: %d.",
                            expectedNumberOfBlocks, numOfBlocks)
            );
        }
    }

    public static LocalDateTime parseDateTime(String dateTimePart) throws RestaurantException {
        try {
            return LocalDateTime.parse(dateTimePart.trim());
        } catch (DateTimeParseException e) {
            throw new RestaurantException("Chybně zadané datum [" + dateTimePart + "]");
        }
    }

    public static Dish getDishById(long dishId) throws RestaurantException {
        try {
            return CookBook.getDishByIdFromCookBook(dishId);
        } catch (RestaurantException e) {
            throw new RestaurantException(e.getMessage());
        }
    }
}


