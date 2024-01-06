package com.engeto.restaurant.model;

import com.engeto.restaurant.util.RestaurantException;
import com.engeto.restaurant.util.RestaurantUtils;
import java.math.BigDecimal;

public class Dish {
    private static long lastAssignedId = 1;
    private long id;
    private String title;
    private BigDecimal price;
    private int preparationTimeInMinutes;
    private String image;

    public Dish(String title, BigDecimal price, int preparationTimeInMinutes, String image) throws RestaurantException {
        this.id = lastAssignedId++;
        this.title = title;
        setPrice(price);
        setPreparationTimeInMinutes(preparationTimeInMinutes);
        this.image = image;
        CookBook.addDishToCookBook(this);
    }

    public Dish(String title, BigDecimal price, int preparationTimeInMinutes) throws RestaurantException {
        this(title, price, preparationTimeInMinutes, "blank");
    }

    private Dish(long id, String title, BigDecimal price, int preparationTimeInMinutes, String image) throws RestaurantException {
        this.id = id;
        this.title = title;
        setPrice(price);
        setPreparationTimeInMinutes(preparationTimeInMinutes);
        this.image = image;
    }

    public static Dish createDishWithId(long id, String title, BigDecimal price, int preparationTimeInMinutes, String image) throws RestaurantException {
        return new Dish(id, title, price, preparationTimeInMinutes, image);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) throws RestaurantException {
        RestaurantUtils.validatePrice(price);
        this.price = price;
    }

    public int getPreparationTimeInMinutes() {
        return preparationTimeInMinutes;
    }

    public void setPreparationTimeInMinutes(int preparationTimeInMinutes) throws RestaurantException {
        RestaurantUtils.validatePreparationTime(preparationTimeInMinutes);
        this.preparationTimeInMinutes = preparationTimeInMinutes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}

