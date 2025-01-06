package com.bitcoinchecker.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

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
        fireTableRowsInserted(addresses.size() - 1, addresses.size() - 1);
    }

    public void updateAddress(BitcoinAddress address) {
        for (int i = 0; i < addresses.size(); i++) {
            if (addresses.get(i).getAddress().equals(address.getAddress())) {
                addresses.set(i, address);
                fireTableRowsUpdated(i, i);
                break;
            }
        }
    }

    public void clear() {
        int size = addresses.size();
        addresses.clear();
        if (size > 0) {
            fireTableRowsDeleted(0, size - 1);
        }
    }
}