import java.util.Scanner;
import java.util.List;

public class SalesApp {
    public static void main(String[] args) {
        SalesProcessor processor = new SalesProcessor();
        processor.loadData("sales.csv", "customers.csv", "products.csv");

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Меню действий ===");
            System.out.println("1. Показать общую сумму продаж");
            System.out.println("2. Показать самые популярные и непопулярные товары");
            System.out.println("3. Показать активных покупателей");
            System.out.println("4. Показать тенденции продаж по месяцам");
            System.out.println("5. Сгенерировать отчет");
            System.out.println("6. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n=== Общая сумма продаж ===");
                    System.out.printf("Общая сумма продаж: %.2f%n", processor.calculateTotalSales());
                    break;

                case 2:
                    System.out.print("\nВведите количество популярных/непопулярных товаров для отображения: ");
                    int count = scanner.nextInt();
                    List<List<Product>> topBottomProducts = processor.getTopAndBottomProducts(count);

                    System.out.println("\n=== Популярные товары ===");
                    topBottomProducts.get(0).forEach(product -> {
                        if (product != null) {
                            System.out.printf("%s - Цена: %.2f%n", product.getName(), product.getPrice());
                        }
                    });

                    System.out.println("\n=== Непопулярные товары ===");
                    topBottomProducts.get(1).forEach(product -> {
                        if (product != null) {
                            System.out.printf("%s - Цена: %.2f%n", product.getName(), product.getPrice());
                        }
                    });
                    break;

                case 3:
                    System.out.print("\nВведите минимальную сумму для активных покупателей: ");
                    double threshold = scanner.nextDouble();
                
                    System.out.println("\n=== Активные покупатели ===");
                    List<Customer> activeCustomers = processor.getActiveCustomers(threshold);
                    if (activeCustomers.isEmpty()) {
                        System.out.println("Нет покупателей, набравших сумму покупок выше указанной.");
                    } else {
                        activeCustomers.forEach(customer -> {
                            // Сумма покупок покупателя
                            double totalSpent = processor.getTotalSpentByCustomer(customer.getCustomerId());
                            System.out.printf("  %s (ID: %s) - Сумма покупок: %.2f%n", customer.getName(), customer.getCustomerId(), totalSpent);
                        });
                    }
                    break;

                case 4:
                    System.out.println("\n=== Тенденции продаж по месяцам ===");
                    processor.analyzeSalesTrends().forEach((key, value) -> 
                        System.out.printf("%s: %d продаж(а/и)%n", key, value));
                    break;

                case 5:
                    System.out.print("\nВведите имя файла для отчета (например, report.txt): ");
                    scanner.nextLine(); // очистить буфер
                    String fileName = scanner.nextLine();

                    processor.generateReport(fileName);
                    System.out.println("Отчет успешно создан и сохранен в '" + fileName + "'.");
                    break;

                case 6:
                    System.out.println("Выход из программы. До свидания!");
                    exit = true;
                    break;

                default:
                    System.out.println("Неверный выбор. Попробуйте еще раз.");
            }
        }

        scanner.close();
    }
}
