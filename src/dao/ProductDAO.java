package dao;

import model.Product;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the 'products' table.
 * Handles all database operations for:
 *   - Module 3: Upload Digital Product
 *   - Module 4: View Product List
 *   - Module 5: Download Product
 */
public class ProductDAO {

    // ── Module 3: Upload Digital Product ─────────────────────────────────────

    /**
     * Saves a newly uploaded product record to the database.
     * The actual file is stored on the server; file_path holds its location.
     *
     * @param product Product object with sellerId, name, description, price, filePath
     * @return true if saved successfully
     */
    public boolean uploadProduct(Product product) {
        String sql = "INSERT INTO products (seller_id, product_name, description, price, file_path) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,    product.getSellerId());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getPrice());
            ps.setString(5, product.getFilePath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProductDAO.uploadProduct] Error: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    // ── Module 4: View Product List ───────────────────────────────────────────

    /**
     * Retrieves all available products from the database.
     * Used to display the product listing page.
     *
     * @return List of all Product objects
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY product_id DESC";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO.getAllProducts] Error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
        return products;
    }

    /**
     * Retrieves all products uploaded by a specific seller.
     * Used on the seller's dashboard.
     *
     * @param sellerId ID of the seller
     * @return List of products belonging to that seller
     */
    public List<Product> getProductsBySeller(int sellerId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE seller_id = ? ORDER BY product_id DESC";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO.getProductsBySeller] Error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
        return products;
    }

    // ── Module 5: Download Product ────────────────────────────────────────────

    /**
     * Fetches a single product by its ID.
     * Used to retrieve file_path before streaming the file to the user.
     *
     * @param productId ID of the product to download
     * @return Product object, or null if not found
     */
    public Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO.getProductById] Error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    // ── Private Helper ────────────────────────────────────────────────────────

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getInt("seller_id"),
            rs.getString("product_name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getString("file_path")
        );
    }
}
