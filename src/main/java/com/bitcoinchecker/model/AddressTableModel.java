package com.bitcoinchecker.model;

import com.bitcoinchecker.db.DatabaseManager;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel for displaying scan results.
 * Extends AbstractTableModel to provide view with:
 * - Address details
 * - Abuse counts
 * - Report URLs
 * Handles data updates and table refresh.
 */
public class AddressTableModel extends AbstractTableModel {
    private final List<BitcoinAddress> addresses = new ArrayList<>();
    private final String[] columnNames = {"Address", "# of abuses", "Link"};

    @Override
    public int getRowCount() {
        return addresses.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column == 2 ? String.class : Object.class; // Make URL column copyable as text
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 2; // Only URL column is editable for copying
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        BitcoinAddress address = addresses.get(row);
        return switch (column) {
            case 0 -> address.getAddress();
            case 1 -> address.getAbuseCount();
            case 2 -> address.getReportUrl();
            default -> null;
        };
    }

    public void addAddress(BitcoinAddress address) {
        addresses.add(address);
        DatabaseManager.getInstance().saveScanResult(address);
        fireTableRowsInserted(addresses.size() - 1, addresses.size() - 1);
    }

    public void clear() {
        int size = addresses.size();
        addresses.clear();
        if (size > 0) {
            fireTableRowsDeleted(0, size - 1);
        }
    }

    public List<BitcoinAddress> getAddresses() {
        return new ArrayList<>(addresses);
    }
}