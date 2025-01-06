package com.bitcoinchecker.controller;

import com.bitcoinchecker.db.DatabaseManager;
import com.bitcoinchecker.model.AddressListTableModel;
import com.bitcoinchecker.model.AddressScannerModel;
import com.bitcoinchecker.model.AddressTableModel;
import com.bitcoinchecker.model.BitcoinAddress;
import com.bitcoinchecker.view.AddressScannerView;

import javax.swing.*;
import java.io.IOException;

public class AddressScannerController {
    private final AddressScannerModel model;
    private final AddressScannerView view;
    private final AddressTableModel tableModel;
    private final AddressListTableModel addressesModel;

    public AddressScannerController(AddressScannerModel model, AddressScannerView view,
                                    AddressTableModel tableModel, AddressListTableModel addressesModel) {
        this.model = model;
        this.view = view;
        this.tableModel = tableModel;
        this.addressesModel = addressesModel;
        initializeController();
    }

    private void initializeController() {
        view.getAddButton().addActionListener(e -> handleAddAddress());
        view.getUploadButton().addActionListener(e -> handleFileUpload());
        view.getClearButton().addActionListener(e -> handleClear());
        view.getScanButton().addActionListener(e -> handleScan());
        view.getSaveButton().addActionListener(e -> handleSave());
    }

    private void handleSave() {
    }

    private void handleScan() {
    }

    private void handleClear() {
        model.clearAddresses();
        tableModel.clear();
        addressesModel.clear();
        DatabaseManager.getInstance().deleteAllAddresses();

    }

    private void handleFileUpload() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                model.loadAddressesFromFile(fileChooser.getSelectedFile());
                int duplicates = 0;

                for (BitcoinAddress address : model.getAddresses()) {
                    String addr = address.getAddress();
                    if (!addressesModel.containsAddress(addr)) {
                        addressesModel.addAddress(addr);
                    } else {
                        duplicates++;
                    }
                }

                if (duplicates > 0) {
                    JOptionPane.showMessageDialog(view,
                            String.format("Upload complete. Skipped %d duplicate addresses.", duplicates));
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view, "Error loading file: " + e.getMessage());
            }
        }
    }

    private void handleAddAddress() {
        String address = view.getAddressFieldText();
        if (!address.isEmpty()) {
            model.addAddress(address);
            addressesModel.addAddress(address);
            view.clearAddressField();
        }
    }

    // ... other handler methods
}