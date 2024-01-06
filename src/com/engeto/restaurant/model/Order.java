package com.engeto.restaurant.model;

import com.engeto.restaurant.manager.RestaurantManager;
import com.engeto.restaurant.util.RestaurantException;
import com.engeto.restaurant.util.RestaurantUtils;
import java.time.LocalDateTime;

public class Order {

    private static long nextId = 1;
    private long id;
    private Table table;
    private Dish orderedDish;
    private int quantity;
    private LocalDateTime orderedTime;
    private LocalDateTime fulfilmentTime;
    private boolean isPaid;


    public Order(Dish orderedDish, int quantity, int tableNumber, LocalDateTime orderedTime, LocalDateTime fulfilmentTime, boolean isPaid) throws RestaurantException {
        this.id = nextId++;
        this.orderedDish = orderedDish;
        setQuantity(quantity);
        try {
            this.table = Table.getTableByNumber(tableNumber, RestaurantManager.getTableList());
        } catch (RestaurantException e) {
            throw new RestaurantException("Chyba při vytváření objednávky: " + e.getMessage());
        }
        this.orderedTime = orderedTime;
        this.fulfilmentTime = fulfilmentTime;
        this.isPaid = isPaid;
        RestaurantManager.addOrderToOrderList(this);
    }

    public Order(Dish dish, int quantity, int tableNumber) throws RestaurantException {
        this(dish, quantity, tableNumber, LocalDateTime.now(), null, false);
    }

    public long getId() {
        return id;
    }

    public Dish getOrderedDish() {
        return orderedDish;
    }

    public void setOrderedDish(Dish orderedDish) {
        this.orderedDish = orderedDish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) throws RestaurantException {
        if (quantity <= 0) {
            throw new RestaurantException("Počet objednaných kusů musí být větší než nula. Zadáno: " + quantity);
        }
        this.quantity = quantity;
    }

    public LocalDateTime getOrderedTime() {
        return orderedTime;
    }

    public void setOrderedTime(LocalDateTime orderedTime) throws RestaurantException {
        RestaurantUtils.validateTime(orderedTime, "Čas vzniku objednávky");
        this.orderedTime = orderedTime;
    }

    public LocalDateTime getFulfilmentTime() {
        return fulfilmentTime;
    }

    public void setFulfilmentTime(LocalDateTime fulfilmentTime) throws RestaurantException {
        if (fulfilmentTime != null) {
            RestaurantUtils.validateTime(fulfilmentTime, "Čas vyřízení objednávky");
            if (fulfilmentTime.isBefore(orderedTime)) {
                throw new RestaurantException("Čas vyřízení objednávky nesmí být před časem vzniku objednávky.");
            }
        }
        this.fulfilmentTime = fulfilmentTime;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public void setAsPaid() {
        isPaid = true;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void fulfilOrder() {
        fulfilmentTime = LocalDateTime.now();
    }

}
