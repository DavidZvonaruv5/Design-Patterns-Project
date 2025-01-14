package com.bitcoinchecker.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.bitcoinchecker.model.BitcoinAddress;


/**
 * SQLite database manager implementing Singleton pattern.
 * Provides:
 * - CRUD operations for addresses
 * - Table initialization
 * - Connection management
 * Uses prepared statements for SQL injection prevention.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:addresses.db";

    private DatabaseManager() {
        initDatabase();
    }

    private static class InstanceHolder {
        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }

    public static DatabaseManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS addresses (address TEXT PRIMARY KEY)");
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS scan_results (" +
                            "address TEXT PRIMARY KEY," +
                            "abuse_count INTEGER," +
                            "report_url TEXT)");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public void deleteAllData() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.createStatement().execute("DELETE FROM addresses");
            conn.createStatement().execute("DELETE FROM scan_results");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }

    public void updateAddress(String oldAddress, String newAddress) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE addresses SET address = ? WHERE address = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newAddress);
                pstmt.setString(2, oldAddress);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update address", e);
        }
    }

    public void saveAddress(String address) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT OR REPLACE INTO addresses (address) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, address);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save address", e);
        }
    }

    public void saveScanResult(BitcoinAddress address) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT OR REPLACE INTO scan_results (address, abuse_count, report_url) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, address.getAddress());
                pstmt.setInt(2, address.getAbuseCount());
                pstmt.setString(3, address.getReportUrl());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save scan result", e);
        }
    }

    public void deleteAddress(String address) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM addresses WHERE address = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, address);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete address", e);
        }
    }

    public List<String> loadAddresses() {
        List<String> addresses = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT address FROM addresses")) {
            while (rs.next()) {
                addresses.add(rs.getString("address"));
            }
            return addresses;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load addresses", e);
        }
    }

    public List<BitcoinAddress> loadScanResults() {
        List<BitcoinAddress> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM scan_results")) {
            while (rs.next()) {
                BitcoinAddress address = new BitcoinAddress(rs.getString("address"));
                address.setAbuseCount(rs.getInt("abuse_count"));
                address.setReportUrl(rs.getString("report_url"));
                results.add(address);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load scan results", e);
        }
    }
}