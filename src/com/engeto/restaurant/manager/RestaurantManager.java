package com.engeto.restaurant.manager;

import com.engeto.restaurant.model.CookBook;
import com.engeto.restaurant.model.Dish;
import com.engeto.restaurant.model.Order;
import com.engeto.restaurant.model.Table;
import com.engeto.restaurant.util.RestaurantException;
import com.engeto.restaurant.util.RestaurantUtils;
import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class RestaurantManager {

    private static List<Order> orderList = new ArrayList<>();
    private static List<Table> tableList = new ArrayList<>();


    public static List<Order> getOrderList() {
        return orderList;
    }

    public static void addOrderToOrderList(Order order) {
        orderList.add(order);
    }

    public static void addTable(Table table) {
        tableList.add(table);
    }

    public static List<Table> getTableList() {
        return tableList;
    }


    // Metoda pro získání počtu aktuálně rozpracovaných objednávek
    public static int getUnfulfilledOrdersCount() {
        return (int) orderList.stream()
                .filter(order -> order.getFulfilmentTime() == null)
                .count();
    }

    // Metoda pro seřazení objednávek podle času zadání
    public static List<Order> getOrdersSortedByTime() {
        List<Order> sortedOrders = new ArrayList<>(orderList);
        sortedOrders.sort(Comparator.comparing(Order::getOrderedTime));
        return sortedOrders;
    }

    public static String formatOrders(List<Order> orders) {
        StringBuilder result = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        if (!orders.isEmpty()) {
            for (Order order : orders) {
                result.append(String.format("%s, kusů: %d, stůl: %d, čas objednání: %s\n",
                        order.getOrderedDish().getTitle(),
                        order.getQuantity(),
                        order.getTable().getTableNumber(),
                        order.getOrderedTime().format(formatter)));
            }
        } else {
            result.append("Žádné objednávky");
        }

        return result.toString();
    }

    // Metoda pro výpočet průměrné doby zpracování objednávek
    public static String getAverageFulfilmentTime() {
        List<Order> fulfilledOrders = orderList.stream()
                .filter(order -> order.getFulfilmentTime() != null)
                .toList();

        if (fulfilledOrders.isEmpty()) {
            return "Žádné splněné objednávky.";
        }

        Duration totalFulfilmentTime = Duration.ZERO;
        for (Order order : fulfilledOrders) {
            totalFulfilmentTime = totalFulfilmentTime.plus(Duration.between(order.getOrderedTime(), order.getFulfilmentTime()));
        }

        long averageSeconds = totalFulfilmentTime.dividedBy(fulfilledOrders.size()).getSeconds();
        long hours = averageSeconds / 3600;
        long minutes = (averageSeconds % 3600) / 60;
        long seconds = averageSeconds % 60;

        return String.format("%d hodin, %d minut, %d sekund", hours, minutes, seconds);
    }

    // Metoda pro získání seznamu jídel objednaných dnes
    public static Set<String> getOrderedDishesToday() {
        LocalDateTime todayMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        Set<String> orderedDishes = new HashSet<>();
        for (Order order : orderList) {
            if (order.getOrderedTime().isAfter(todayMidnight) && order.getOrderedDish() != null) {
                orderedDishes.add(order.getOrderedDish().getTitle());
            }
        }

        return orderedDishes;
    }

    public static BigDecimal getTotalConsumptionCostForTable(int tableNumber) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Order order : orderList) {
            if (order.getTable().getTableNumber() == tableNumber) {
                totalCost = totalCost.add(order.getOrderedDish().getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
            }
        }

        return totalCost;
    }

    // Metoda pro export objednávek pro jeden stůl ve specifikovaném formátu
    public static String exportOrdersForTable(int tableNumber) {
        StringBuilder result = new StringBuilder();
        appendTableHeader(result, tableNumber);

        int itemNumber = 1;
        for (Order order : orderList) {
            Table orderTable = order.getTable();
            if (orderTable.getTableNumber() == tableNumber) {
                appendOrderDetails(result, order, itemNumber);
                itemNumber++;
            }
        }

        appendTableFooter(result);
        return result.toString();
    }

    private static void appendTableHeader(StringBuilder result, int tableNumber) {
        result.append("** Objednávky pro stůl č. ");
        result.append(String.format("%2d **", tableNumber)).append("\n");
        result.append("****").append("\n");
    }

    private static void appendOrderDetails(StringBuilder result, Order order, int itemNumber) {
        BigDecimal totalPrice = order.getOrderedDish().getPrice().multiply(BigDecimal.valueOf(order.getQuantity()));
        String title = order.getQuantity() > 1 ?
                String.format("%s %dx", order.getOrderedDish().getTitle(), order.getQuantity()) :
                order.getOrderedDish().getTitle();

        result.append(String.format("%d. %s (%.2f Kč):\t%tH:%tM-%tH:%tM\t", itemNumber,
                title, totalPrice, order.getOrderedTime(), order.getOrderedTime(), order.getFulfilmentTime(), order.getFulfilmentTime()));
        if (order.isPaid()) {
            result.append("zaplaceno");
        }
        result.append("\n");
    }

    private static void appendTableFooter(StringBuilder result) {
        result.append("******").append("\n");
    }

    public static void saveDataToFile(String recipeFileName, String orderFileName) {
        saveDishesToFile(recipeFileName);
        saveOrdersToFile(orderFileName);
    }

    public static void saveDishesToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, Dish> entry : CookBook.getAllDishes().entrySet()) {
                Dish dish = entry.getValue();
                String line = String.format("%s;%s;%s;%s;%s",
                        dish.getId(), dish.getTitle(), dish.getPrice(),
                        dish.getPreparationTimeInMinutes(), dish.getImage());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException | RestaurantException e) {
            System.err.println("Chyba při ukládání jídel do souboru: " + e.getMessage());
        }
    }

    public static void saveOrdersToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            for (Order order : orderList) {
                Table orderTable = order.getTable();
                int tableNumber = orderTable.getTableNumber();
                String line = String.format("%s;%s;%s;%s;%s;%s;%s",
                        order.getId(), tableNumber, order.getOrderedDish().getId(),
                        order.getQuantity(), order.getOrderedTime(), order.getFulfilmentTime(), order.isPaid());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Chyba při ukládání objednávek do souboru: " + e.getMessage());
        }
    }

    public static void loadDataFromFile(String dishFileName, String orderFileName) throws RestaurantException {
        try {
            loadDishesFromFile(dishFileName);
            loadOrdersFromFile(orderFileName);
        } catch (RestaurantException e) {
            System.err.println("Chyba při načítání evidence ze souboru - " + e.getMessage());
        }
    }

    private static void loadDishesFromFile(String fileName) throws RestaurantException {
        int lineNumber = 1;
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                RestaurantUtils.validateNumberOfBlocks(parts, lineNumber, 5, "pokrmů");
                long id = Long.parseLong(parts[0].trim());
                String title = parts[1].trim();
                BigDecimal price = RestaurantUtils.parsePrice(parts[2].trim(), lineNumber, line);
                int preparationTime = RestaurantUtils.parsePreparationTime(parts[3].trim(), lineNumber, line);
                String image = parts[4].trim();

                Dish dish = Dish.createDishWithId(id, title, price, preparationTime, image);
                CookBook.addDishToCookBook(dish);
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Nepodařilo se nalézt soubor s jídly: " + e.getLocalizedMessage());
        }
    }

    public static void loadOrdersFromFile(String fileName) throws RestaurantException {
        int lineNumber = 1;

        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                parseLineOrder(line, lineNumber);
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Nepodařilo se nalézt soubor s objednávkami: "+e.getLocalizedMessage());
        }
    }


    private static void parseLineOrder(String line, int lineNumber) throws RestaurantException {
        try {
            String[] parts = line.split(";");
            RestaurantUtils.validateNumberOfBlocks(parts, lineNumber, 7, "objednávek");

            int tableNumber = Integer.parseInt(parts[1].trim());
            long dishId = Long.parseLong(parts[2].trim());
            int quantity = Integer.parseInt(parts[3].trim());
            boolean isPaid = Boolean.parseBoolean(parts[5].trim());

            Dish dish = RestaurantUtils.getDishById(parts[2].trim(), dishId);
            LocalDateTime orderedTime = RestaurantUtils.parseDateTime(parts[4].trim(), line);
            LocalDateTime fulfilmentTime = null;
            if (!parts[5].trim().isEmpty() && !parts[5].trim().equalsIgnoreCase("null")) {
                fulfilmentTime = RestaurantUtils.parseDateTime(parts[5].trim(), line);
            }
            createOrder(dish, quantity, tableNumber, orderedTime, fulfilmentTime, isPaid);
        } catch (RestaurantException e) {
            throw new RestaurantException("Chyba při načítání objednávek ze souboru na řádku č. " + lineNumber + ": " + e.getMessage());
        }
    }

    private static void createOrder(Dish dish, int quantity, int tableNumber, LocalDateTime orderedTime,
                                    LocalDateTime fulfilmentTime, boolean isPaid) throws RestaurantException {
        Order order = new Order(dish, quantity, tableNumber);
        order.setOrderedTime(orderedTime);
        order.setFulfilmentTime(fulfilmentTime);
        order.setPaid(isPaid);
    }
}