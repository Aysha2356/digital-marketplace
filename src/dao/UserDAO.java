package dao;

import model.User;
import util.DBConnection;

import java.sql.*;

/**
 * Data Access Object for the 'users' table.
 * Handles all database operations for:
 *   - Module 1: User Registration
 *   - Module 2: User Login
 */
public class UserDAO {

    // ── Module 1: User Registration ───────────────────────────────────────────

    /**
     * Inserts a new user into the database.
     * Password should be hashed before calling this method (see PasswordUtil).
     *
     * @param user User object with name, email, hashed password, role
     * @return true if inserted successfully, false otherwise
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());   // Must be hashed by caller
            ps.setString(4, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO.registerUser] Error: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Checks whether an email is already registered.
     * Used during registration to prevent duplicate accounts.
     *
     * @param email Email to check
     * @return true if email already exists
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[UserDAO.isEmailExists] Error: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    // ── Module 2: User Login ──────────────────────────────────────────────────

    /**
     * Validates user credentials during login.
     * Compares email and hashed password against the database.
     *
     * @param email    User's email
     * @param hashedPw Hashed version of the entered password
     * @return User object if credentials match, null if invalid
     */
    public User loginUser(String email, String hashedPw) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, hashedPw);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
            return null;  // Login failed
        } catch (SQLException e) {
            System.err.println("[UserDAO.loginUser] Error: " + e.getMessage());
            return null;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}
