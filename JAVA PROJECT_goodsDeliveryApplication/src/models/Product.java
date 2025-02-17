package models;

public class Product {
    private String name;
    private String description;
    private double pricePerKg;
    private String imageUrl;
    private int stockQuantity;
    private int productId;

    public Product(int productId, String name, String description, double pricePerKg,
                  String imageUrl, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.pricePerKg = pricePerKg;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPricePerKg() { return pricePerKg; }
    public String getImageUrl() { return imageUrl; }
    public int getStockQuantity() { return stockQuantity; }
    public int getProductId() { return productId; }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
