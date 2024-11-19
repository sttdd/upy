import java.time.LocalDateTime;

public class Sale {
    private String saleId;         // Идентификатор продажи
    private LocalDateTime dateTime; // Дата и время продажи
    private String customerId;      // Идентификатор покупателя
    private String productId;       // Идентификатор товара

    // Конструктор для создания объекта Sale
    public Sale(String saleId, LocalDateTime dateTime, String customerId, String productId) {
        this.saleId = saleId;
        this.dateTime = dateTime;
        this.customerId = customerId;
        this.productId = productId;
    }

    // Геттеры для доступа к полям
    public String getSaleId() {
        return saleId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "saleId='" + saleId + '\'' +
                ", dateTime=" + dateTime +
                ", customerId='" + customerId + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}
