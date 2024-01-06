import com.engeto.restaurant.manager.RestaurantManager;
import com.engeto.restaurant.model.CookBook;
import com.engeto.restaurant.model.Dish;
import com.engeto.restaurant.model.Order;
import com.engeto.restaurant.model.Table;
import com.engeto.restaurant.util.RestaurantException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws RestaurantException {

        createTables(15);

        //1. Načti stav evidence z disku (Pokud se aplikace spouští poprvé a soubory neexistují, budou veškeré seznamy a repertoár zatím prázdné.)
        RestaurantManager.loadDataFromFile("cookbook.txt", "orders.txt");

        //2. Připrav testovací data.
        createDishes();
        createOrders();

        //3. Vypiš celkovou cenu konzumace pro stůl číslo 15.
        System.out.println("Celková cena konzumace pro daný stůl: " + RestaurantManager.getTotalConsumptionCostForTable(15) + " Kč.");

        //4. Použij všechny připravené metody pro získání informací pro management — údaje vypisuj na obrazovku.
        System.out.println("\nNezpracovaných objednávek: " + RestaurantManager.getUnfulfilledOrdersCount());
        System.out.println("\nSeřazené objednávky podle času zadání: \n" + RestaurantManager.formatOrders(RestaurantManager.getOrdersSortedByTime()));
        System.out.println("\nPrůměrná doba zpracování objednávek: " + RestaurantManager.getAverageFulfilmentTime());
        System.out.println("\nSeznam jídel, která byla dnes objednána (bez duplicit): \n" + RestaurantManager.getOrderedDishesToday());
        System.out.println("\nExport seznamu objednávek pro jeden stůl: \n" + RestaurantManager.exportOrdersForTable(4));
        System.out.println(CookBook.getAllDishesAsString());

        //5. Změněná data ulož na disk.
        RestaurantManager.saveDataToFile("cookbook.txt", "orders.txt");

        //6. Po opětovném spuštění aplikace musí být data opět v pořádku načtena. (Vyzkoušej!)

    }


    private static void createDishes() throws RestaurantException {
        Dish rizek = new Dish("Kuřecí řízek obalovaný 150 g", BigDecimal.valueOf(200), 30);
        Dish hranolky = new Dish("Hranolky 150 g", BigDecimal.valueOf(100), 20);
        Dish pstruh = new Dish("Pstruh na víně 200 g", BigDecimal.valueOf(300), 45);
        Dish kofola = new Dish("Kofola 0,5 l", BigDecimal.valueOf(50), 5);
        Dish voda = new Dish("Minerální voda jemně perlivá 0,25 l", BigDecimal.valueOf(30), 5);
        Dish pizza = new Dish("Pizza Grande", BigDecimal.valueOf(130), 35);
        Dish nanuk = new Dish("Nanuk Míša", BigDecimal.valueOf(30), 2);

    }

    private static void createOrders() throws RestaurantException {
        Dish rizek = CookBook.getDishByIdFromCookBook(1);
        Dish hranolky = CookBook.getDishByIdFromCookBook(2);
        Dish pstruh = CookBook.getDishByIdFromCookBook(3);
        Dish kofola = CookBook.getDishByIdFromCookBook(4);
        Dish voda = CookBook.getDishByIdFromCookBook(5);
        Dish pizza = CookBook.getDishByIdFromCookBook(6);
        Dish nanuk = CookBook.getDishByIdFromCookBook(7);

        Order one = new Order(rizek, 2, 15);
        Order two = new Order(hranolky, 2, 15);
        Order three = new Order(kofola, 2, 15);
        three.fulfilOrder();

        Order four = new Order(pstruh, 3, 2, LocalDateTime.of(2023,12,19,15,38,00),  LocalDateTime.of(2023,12,19,16,21),false);
        Order five = new Order(voda, 3, 2,  LocalDateTime.of(2023,12,19,15,38,00),  LocalDateTime.of(2023,12,19,15,45),false);

        Order six = new Order(kofola, 4, 4, LocalDateTime.of(2023,12,20,10,25,00),  LocalDateTime.of(2023,12,20,10,29),true);
        Order seven = new Order(pizza, 1, 4, LocalDateTime.of(2023,12,20,10,29,00),  LocalDateTime.of(2023,12,20,11,10),true);
        Order eight = new Order(nanuk, 1, 4, LocalDateTime.of(2023,12,20,11,29,00),  LocalDateTime.of(2023,12,20,11,35),false);
    }

    private static void createTables(int numberOfTables) {
        for (int i = 1; i <= numberOfTables; i++) {
            new Table(i);
        }
    }
}