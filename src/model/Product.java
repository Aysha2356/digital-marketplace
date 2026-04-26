package model;

/**
 * Model class representing a Digital Product in the Marketplace.
 * Maps to the 'products' table in MySQL.
 *
 * Table: products (product_id, seller_id, product_name, description, price, file_path)
 */
public class Product {

    private int    productId;
    private int    sellerId;
    private String productName;
    private String description;
    private double price;
    private String filePath;   // Server-side path to the uploaded file

    // ── Constructors ──────────────────────────────────────────────────────────

    public Product() {}

    public Product(int sellerId, String productName, String description, double price, String filePath) {
        this.sellerId    = sellerId;
        this.productName = productName;
        this.description = description;
        this.price       = price;
        this.filePath    = filePath;
    }

    public Product(int productId, int sellerId, String productName, String description, double price, String filePath) {
        this.productId   = productId;
        this.sellerId    = sellerId;
        this.productName = productName;
        this.description = description;
        this.price       = price;
        this.filePath    = filePath;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getProductId()                       { return productId; }
    public void   setProductId(int productId)          { this.productId = productId; }

    public int    getSellerId()                        { return sellerId; }
    public void   setSellerId(int sellerId)            { this.sellerId = sellerId; }

    public String getProductName()                     { return productName; }
    public void   setProductName(String productName)   { this.productName = productName; }

    public String getDescription()                     { return description; }
    public void   setDescription(String description)   { this.description = description; }

    public double getPrice()                           { return price; }
    public void   setPrice(double price)               { this.price = price; }

    public String getFilePath()                        { return filePath; }
    public void   setFilePath(String filePath)         { this.filePath = filePath; }

    @Override
    public String toString() {
        return "Product{productId=" + productId + ", productName='" + productName
                + "', price=" + price + ", sellerId=" + sellerId + "}";
    }
}
