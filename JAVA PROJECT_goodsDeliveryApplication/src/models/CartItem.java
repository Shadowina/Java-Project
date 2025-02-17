package models;

public class CartItem {
    private final int cartId;
    private final int productId;
    private final String productName;
    private final double pricePerKg;
    private final double quantity;
    private final double total;

    public CartItem(int cartId, int productId, String productName,
                   double pricePerKg, double quantity, double total) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.pricePerKg = pricePerKg;
        this.quantity = quantity;
        this.total = total;
    }

    // Getters
    public int getCartId() { return cartId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPricePerKg() { return pricePerKg; }
    public double getQuantity() { return quantity; }
    public double getTotal() { return total; }
}
