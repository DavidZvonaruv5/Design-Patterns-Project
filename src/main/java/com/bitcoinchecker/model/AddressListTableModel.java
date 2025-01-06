package com.bitcoinchecker.model;

import com.bitcoinchecker.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AddressListTableModel extends AbstractTableModel {
    private final List<String> addresses = new ArrayList<>();
    private final String[] columnNames = {"Address"};

    @Override
    public int getRowCount() { return addresses.size(); }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 0;  // Makes Address column editable
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column == 1 ? Object.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) return addresses.get(row);
        return "Actions";
    }
    public void clear() {
        int size = addresses.size();
        addresses.clear();
        if (size > 0) {
            fireTableRowsDeleted(0, size - 1);
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 0 && value instanceof String) {
            String newAddress = (String) value;
            String oldAddress = addresses.get(row);
            updateAddress(row, newAddress);
            DatabaseManager.getInstance().updateAddress(oldAddress, newAddress);
        }
    }

    public List<String> getAddresses() {
        return new ArrayList<>(addresses);
    }

    public void updateAddress(int row, String newAddress) {
        addresses.set(row, newAddress);
        fireTableRowsUpdated(row, row);
    }

    public String getAddressAt(int row) {
        return addresses.get(row);
    }

    public void addAddress(String address) {
        addresses.add(address);
        DatabaseManager.getInstance().saveAddress(address);
        fireTableRowsInserted(addresses.size() - 1, addresses.size() - 1);
    }

    public void removeAddress(int row) {
        String address = addresses.remove(row);
        DatabaseManager.getInstance().deleteAddress(address);
        fireTableRowsDeleted(row, row);
    }

    public boolean containsAddress(String address) {
        return addresses.contains(address);
    }
}