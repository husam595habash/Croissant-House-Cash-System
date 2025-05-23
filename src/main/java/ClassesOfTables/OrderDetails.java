package ClassesOfTables;

public class OrderDetails {
    private int itemId;
    private String itemName;
    private String categoryName;
    private double pricePerUnit;
    private int quantity;
    private double totalPrice;

    // Constructor
    public OrderDetails(int itemId, String itemName, String categoryName, double pricePerUnit, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.categoryName = categoryName;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.totalPrice = pricePerUnit * quantity;
    }

    // Getters and Setters
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        recalculateTotalPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    private void recalculateTotalPrice() {
        this.totalPrice = this.pricePerUnit * this.quantity;
    }
}
