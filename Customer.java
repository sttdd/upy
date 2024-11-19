public class Customer {
    private String customerId; // Идентификатор покупателя
    private String name;       // Имя покупателя

    // Конструктор для создания объекта Customer
    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    // Геттеры для доступа к полям
    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
