package com.bitcoinchecker.view;

import com.bitcoinchecker.model.*;
import com.bitcoinchecker.observer.ScanObserver;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class AddressScannerView extends JFrame implements ScanObserver {
    // UI components only
    private final JTextField addressField;
    private final JButton addButton;
    private final JButton uploadButton;
    private final JButton clearButton;
    private final JButton scanButton;
    private final JButton saveButton;
    private final JTable resultsTable;
    private final JTextArea logArea;
    private final JTable addressesTable;

    public AddressScannerView(AddressTableModel tableModel, AddressListTableModel addressesModel) {
        setTitle("Bitcoin Address Checker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components first
        addressField = new JTextField(40);
        addButton = new JButton("Add to list");
        uploadButton = new JButton("Upload file");
        clearButton = new JButton("Clear table");
        scanButton = new JButton("Scan");
        saveButton = new JButton("Save results");

        // Initialize tables
        addressesTable = new JTable(addressesModel);
        addressesTable.setFillsViewportHeight(true);
        addressesTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    int row = addressesTable.getSelectedRow();
                    if (row != -1 && JOptionPane.showConfirmDialog(null,
                            "Delete this address?", "Confirm",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        addressesModel.removeAddress(row);
                    }
                }
            }
        });
        resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);

        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);

        setupLayout();
        pack();
        setLocationRelativeTo(null);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Input panel (top)
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(createAddressPanel(), BorderLayout.NORTH);
        inputPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        // Tables panel (center) - side by side
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Addresses table
        JScrollPane addressesPane = new JScrollPane(addressesTable);
        JPanel addressesPanel = new JPanel(new BorderLayout());
        addressesPanel.add(new JLabel("Addresses"), BorderLayout.NORTH);
        addressesPanel.add(addressesPane);

        // Results table
        JScrollPane resultsPane = new JScrollPane(resultsTable);
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.add(new JLabel("Scan Results"), BorderLayout.NORTH);
        resultsPanel.add(resultsPane);

        tablesPanel.add(addressesPanel);
        tablesPanel.add(resultsPanel);

        add(inputPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(createLogPanel(), BorderLayout.SOUTH);
    }

    // Only UI update methods
    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    @Override
    public void onScanStarted() {
        SwingUtilities.invokeLater(() -> {
            scanButton.setEnabled(false);
            addButton.setEnabled(false);
            uploadButton.setEnabled(false);
            appendLog("Scanning started...");
        });
    }

    @Override
    public void onAddressScanned(BitcoinAddress address) {
        SwingUtilities.invokeLater(() -> {
            appendLog("Scanned address: " + address.getAddress());
        });
    }

    @Override
    public void onScanCompleted(List<BitcoinAddress> results) {
        SwingUtilities.invokeLater(() -> {
            scanButton.setEnabled(true);
            addButton.setEnabled(true);
            uploadButton.setEnabled(true);
            appendLog("Scan completed successfully");
        });
    }

    @Override
    public void onScanFailed(Exception e) {
        SwingUtilities.invokeLater(() -> {
            scanButton.setEnabled(true);
            addButton.setEnabled(true);
            uploadButton.setEnabled(true);
            appendLog("Scan failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error during scan: " + e.getMessage(),
                    "Scan Error",
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    private JPanel createAddressPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(addButton);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(uploadButton);
        panel.add(clearButton);
        panel.add(scanButton);
        panel.add(saveButton);
        return panel;
    }

    private JScrollPane createLogPanel() {
        JScrollPane scrollPane = new JScrollPane(logArea);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return scrollPane;
    }

    // Getters only - no data manipulation
    public String getAddressFieldText() { return addressField.getText(); }
    public void clearAddressField() { addressField.setText(""); }
    public JButton getAddButton() { return addButton; }
    public JButton getUploadButton() { return uploadButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getScanButton() { return scanButton; }
    public JButton getSaveButton() { return saveButton; }}