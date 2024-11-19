import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class SalesProcessor {

    private List<Sale> sales = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    // Загружает данные из CSV файлов
    public void loadData(String salesFile, String customersFile, String productsFile) {
        loadSales(salesFile);
        loadCustomers(customersFile);
        loadProducts(productsFile);
    }

    // Загрузка данных о продажах
    private void loadSales(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                try {
                    String saleId = values[0];
                    LocalDateTime dateTime = LocalDateTime.parse(values[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String customerId = values[2];
                    String productId = values[3];
                    sales.add(new Sale(saleId, dateTime, customerId, productId));
                } catch (DateTimeParseException e) {
                    System.out.println("Пропущена некорректная строка: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке данных о продажах: " + e.getMessage());
        }
    }


    // Загрузка данных о покупателях
    private void loadCustomers(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String customerId = values[0];
                String name = values[1];
                customers.add(new Customer(customerId, name));
            }
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке данных о покупателях: " + e.getMessage());
        }
    }

    // Загрузка данных о товарах
    private void loadProducts(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isHeader = true;  // Флаг для пропуска заголовка
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;  // Пропускаем первую строку с заголовками
                    continue;
                }
                String[] values = line.split(",");
                try {
                    String productId = values[0];
                    String name = values[1];
                    double price = Double.parseDouble(values[2]);  // Преобразование в double
                    products.add(new Product(productId, name, price));
                } catch (NumberFormatException e) {
                    System.out.println("Пропущена некорректная строка: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке данных о товарах: " + e.getMessage());
        }
    }
    

    // Подсчет общей суммы всех продаж
    public double calculateTotalSales() {
        double total = 0;
        for (Sale sale : sales) {
            Product product = products.stream()
                    .filter(p -> p.getProductId().equals(sale.getProductId()))
                    .findFirst()
                    .orElse(null);
            if (product != null) {
                total += product.getPrice();
            }
        }
        return total;
    }

    // Получение самых популярных и непопулярных товаров
    public List<List<Product>> getTopAndBottomProducts(int count) {
        Map<Product, Long> productSales = sales.stream()
                .map(sale -> products.stream()
                        .filter(product -> product.getProductId().equals(sale.getProductId()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(product -> product, Collectors.counting()));

        List<Product> topProducts = productSales.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Product> bottomProducts = productSales.entrySet().stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return Arrays.asList(topProducts, bottomProducts);
    }

    // Получение активных покупателей, которые потратили более заданной суммы
    public List<Customer> getActiveCustomers(double threshold) {
        return customers.stream()
                .filter(customer -> getTotalSpentByCustomer(customer.getCustomerId()) > threshold)
                .collect(Collectors.toList());
    }

    // Метод для вычисления общей суммы покупок конкретного покупателя
    public double getTotalSpentByCustomer(String customerId) {
        double total = 0;
        for (Sale sale : sales) {
            if (sale.getCustomerId().equals(customerId)) {
                Product product = products.stream()
                        .filter(p -> p.getProductId().equals(sale.getProductId()))
                        .findFirst()
                        .orElse(null);
                if (product != null) {
                    total += product.getPrice();
                }
            }
        }
        return total;
    }

    // Анализ тенденций продаж по месяцам
    public Map<String, Long> analyzeSalesTrends() {
        return sales.stream()
                .collect(Collectors.groupingBy(sale -> sale.getDateTime().getMonth().name(), Collectors.counting()));
    }

    // Генерация отчета и сохранение его в файл
    public void generateReport(String filePath) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            // Заголовок отчета
            writer.println("==================================================");
            writer.println("                    ОТЧЕТ ПО ПРОДАЖАМ");
            writer.println("==================================================\n");

            // Общая сумма продаж
            writer.println("=== Общая сумма продаж ===");
            writer.printf("Общая сумма продаж: %.2f%n", calculateTotalSales());
            writer.println();

            // Популярные и непопулярные товары
            writer.println("=== Популярные и непопулярные товары ===");
            writer.println("Популярные товары:");
            getTopAndBottomProducts(5).get(0).forEach(product -> {
                if (product != null) {
                    writer.printf("  %s - Цена: %.2f%n", product.getName(), product.getPrice());
                }
            });

            writer.println("\nНепопулярные товары:");
            getTopAndBottomProducts(5).get(1).forEach(product -> {
                if (product != null) {
                    writer.printf("  %s - Цена: %.2f%n", product.getName(), product.getPrice());
                }
            });
            writer.println();

            // Активные покупатели
            writer.println("=== Активные покупатели ===");
            getActiveCustomers(1000).forEach(customer -> {
                double totalSpent = getTotalSpentByCustomer(customer.getCustomerId());
                writer.printf("  %s (ID: %s) - Сумма покупок: %.2f%n", customer.getName(), customer.getCustomerId(), totalSpent);
            });
            writer.println();

            // Тенденции продаж
            writer.println("=== Тенденции продаж по месяцам ===");
            analyzeSalesTrends().forEach((month, count) -> 
                writer.printf("  %s: %d продаж%n", month, count));

            writer.println("\n==================================================");
            writer.println("               Конец отчета");
            writer.println("==================================================");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении отчета: " + e.getMessage());
        }
    }
}
