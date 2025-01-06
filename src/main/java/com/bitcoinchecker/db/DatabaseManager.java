package com.bitcoinchecker.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:addresses.db";

    private DatabaseManager() {
        initDatabase();
    }

    public void deleteAllAddresses() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM addresses";
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }

    private static class InstanceHolder {
        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }

    public static DatabaseManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS addresses (address TEXT PRIMARY KEY)";
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public void updateAddress(String oldAddress, String newAddress) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE addresses SET address = ? WHERE address = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newAddress);
            pstmt.setString(2, oldAddress);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update address", e);
        }
    }

    public void saveAddress(String address) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT OR REPLACE INTO addresses (address) VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, address);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save address", e);
        }
    }

    public void deleteAddress(String address) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM addresses WHERE address = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, address);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete address", e);
        }
    }

    public List<String> loadAddresses() {
        List<String> addresses = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT address FROM addresses");
            while (rs.next()) {
                addresses.add(rs.getString("address"));
            }
            return addresses;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load addresses", e);
        }
    }
}