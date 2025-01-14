package com.bitcoinchecker.view;

import com.bitcoinchecker.model.*;
import com.bitcoinchecker.observer.ScanObserver;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.GridLayout;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;


/**
 * View component implementing MVC and Observer patterns.
 * Provides GUI interface for:
 * - Adding/uploading Bitcoin addresses
 * - Displaying address list and scan results
 * - Log display
 * - Scan controls
 * Implements ScanObserver to receive scan status updates.
 * Uses SwingUtilities.invokeLater for thread-safe UI updates.
 */
public class AddressScannerView extends JFrame implements ScanObserver {
    private static final Color BG_COLOR = new Color(200, 200, 200);
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color TABLE_GRID_COLOR = new Color(211, 211, 211);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Font LOG_FONT = new Font("Consolas", Font.PLAIN, 12);
    private static final int LOG_HEIGHT = 200;
    private static final int PADDING = 10;

    private JTextField addressField;
    private JButton addButton;
    private JButton uploadButton;
    private JButton clearButton;
    private JButton scanButton;
    private JButton saveButton;
    private JTable resultsTable;
    private JTable addressesTable;
    private JTextPane logArea;
    private final AddressTableModel tableModel;

    public AddressScannerView(AddressTableModel tableModel, AddressListTableModel addressesModel) {
        this.tableModel = tableModel;
        this.addressField = addressField;
        this.addButton = addButton;
        this.uploadButton = uploadButton;
        this.clearButton = clearButton;
        this.scanButton = scanButton;
        this.saveButton = saveButton;
        this.resultsTable = resultsTable;
        this.addressesTable = addressesTable;
        this.logArea = logArea;

        setupFrame();
        initializeComponents(addressesModel);
        setupLayout();
        configureStyles();
    }

    private void setupFrame() {
        setTitle("Bitcoin Address Checker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(BG_COLOR);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
    }

    private void initializeComponents(AddressListTableModel addressesModel) {
        addressField = new JTextField(40);
        addButton = createButton("Add to list");
        uploadButton = createButton("Upload file");
        clearButton = createButton("Clear table");
        scanButton = createButton("Scan");
        saveButton = createButton("Save results");

        addressesTable = new JTable(addressesModel);
        setupAddressTable(addressesModel);

        resultsTable = new JTable(tableModel);
        setupResultTable();

        logArea = new JTextPane();
        logArea.setEditable(false);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }

    private void setupAddressTable(AddressListTableModel model) {
        addressesTable.setFillsViewportHeight(true);
        addressesTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    deleteSelectedAddress(model);
                }
            }
        });
    }

    private void deleteSelectedAddress(AddressListTableModel model) {
        int row = addressesTable.getSelectedRow();
        if (row != -1 && JOptionPane.showConfirmDialog(this,
                "Delete this address?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            model.removeAddress(row);
        }
    }

    private void setupResultTable() {
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setCellSelectionEnabled(true);
        resultsTable.setRowSelectionAllowed(false);
        resultsTable.setColumnSelectionAllowed(true);
    }

    private void configureStyles() {
        // Buttons
        Arrays.asList(addButton, uploadButton, clearButton, scanButton, saveButton).forEach(button -> {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.BLACK);
            button.setFont(BUTTON_FONT);
        });

        // Tables
        Arrays.asList(addressesTable, resultsTable).forEach(table -> {
            table.setShowGrid(true);
            table.setGridColor(TABLE_GRID_COLOR);
            table.setFont(TABLE_FONT);
            table.setRowHeight(25);
            table.getTableHeader().setBackground(PRIMARY_COLOR);
            table.getTableHeader().setForeground(Color.BLACK);
            table.getTableHeader().setFont(BUTTON_FONT);
        });

        // Text components
        addressField.setFont(TABLE_FONT);
        logArea.setFont(LOG_FONT);
        logArea.setBackground(new Color(250, 250, 250));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(PADDING, PADDING));
        add(createInputPanel(), BorderLayout.NORTH);
        add(createTablesPanel(), BorderLayout.CENTER);
        add(createLogPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(createAddressPanel(), BorderLayout.NORTH);
        panel.add(createButtonPanel(), BorderLayout.SOUTH);
        return panel;
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
        Arrays.asList(uploadButton, clearButton, scanButton, saveButton)
                .forEach(panel::add);
        return panel;
    }

    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, PADDING, 0));
        panel.add(createTablePanel("Addresses", addressesTable));
        panel.add(createTablePanel("Scan Results", resultsTable));
        return panel;
    }

    private JPanel createTablePanel(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title);
        label.setFont(BUTTON_FONT);
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table));
        return panel;
    }

    private JScrollPane createLogPanel() {
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(getWidth(), LOG_HEIGHT));
        return scrollPane;
    }

    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = logArea.getStyledDocument();
                doc.insertString(doc.getLength(), message + "\n", null);
                logArea.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onScanStarted() {
        SwingUtilities.invokeLater(() -> setButtonsEnabled(false));
    }

    @Override
    public void onAddressScanned(BitcoinAddress address) {
        SwingUtilities.invokeLater(() -> {
            tableModel.addAddress(address);
            appendLog("Scanned address: " + address.getAddress());
        });
    }

    @Override
    public void onScanCompleted(List<BitcoinAddress> results) {
        SwingUtilities.invokeLater(() -> setButtonsEnabled(true));
    }

    @Override
    public void onScanFailed(Exception e) {
        SwingUtilities.invokeLater(() -> setButtonsEnabled(true));
    }

    private void setButtonsEnabled(boolean enabled) {
        scanButton.setEnabled(enabled);
        addButton.setEnabled(enabled);
        uploadButton.setEnabled(enabled);
    }

    // Getters
    public String getAddressFieldText() { return addressField.getText(); }
    public void clearAddressField() { addressField.setText(""); }
    public JTextPane getLogArea() { return logArea; }
    public JButton getAddButton() { return addButton; }
    public JButton getUploadButton() { return uploadButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getScanButton() { return scanButton; }
    public JButton getSaveButton() { return saveButton; }
}