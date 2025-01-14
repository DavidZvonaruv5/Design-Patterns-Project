package com.bitcoinchecker.controller;

import com.bitcoinchecker.db.DatabaseManager;
import com.bitcoinchecker.model.AddressListTableModel;
import com.bitcoinchecker.model.AddressScannerModel;
import com.bitcoinchecker.model.AddressTableModel;
import com.bitcoinchecker.model.BitcoinAddress;
import com.bitcoinchecker.util.ExcelExporter;
import com.bitcoinchecker.view.AddressScannerView;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Controller component implementing MVC pattern.
 * Handles:
 * - User interactions (button clicks)
 * - File operations
 * - Scan coordination between models
 * - Error handling and user notifications
 */
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
        List<BitcoinAddress> addresses = tableModel.getAddresses();
        if (addresses.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No scan results to export.");
            return;
        }
        ExcelExporter.exportToExcel(addresses, view);
    }

    private void handleScan() {
        List<String> addresses = addressesModel.getAddresses();
        if (addresses.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No addresses to scan.");
            return;
        }

        model.clearAddresses();
        addresses.forEach(model::addAddress);

        tableModel.clear();
        model.scanAddresses();
    }

    private void handleClear() {
        model.clearAddresses();
        tableModel.clear();
        addressesModel.clear();
        DatabaseManager.getInstance().deleteAllData();
    }

    private void handleFileUpload() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                model.clearAddresses();
                int duplicates = 0;
                for (String line : Files.readAllLines(fileChooser.getSelectedFile().toPath())) {
                    if (!line.trim().isEmpty()) {
                        String addr = line.trim();
                        if (!addressesModel.containsAddress(addr)) {
                            model.addAddress(addr);
                            addressesModel.addAddress(addr);
                        } else {
                            duplicates++;
                        }
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
            System.out.println("Adding address: " + address);
            model.addAddress(address);
            addressesModel.addAddress(address);
            view.clearAddressField();
        }
    }

}