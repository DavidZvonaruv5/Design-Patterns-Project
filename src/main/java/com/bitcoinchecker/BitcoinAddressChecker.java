package com.bitcoinchecker;

import com.bitcoinchecker.api.ChainAbuseClient;
import com.bitcoinchecker.api.response.AddressResponse;
import com.bitcoinchecker.db.DatabaseManager;
import com.bitcoinchecker.model.AddressListTableModel;
import com.bitcoinchecker.model.AddressScannerModel;
import com.bitcoinchecker.model.AddressTableModel;
import com.bitcoinchecker.util.Config;
import com.bitcoinchecker.view.AddressScannerView;
import com.bitcoinchecker.controller.AddressScannerController;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.List;

public class BitcoinAddressChecker {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                try {
                    AddressScannerModel model = new AddressScannerModel();
                    AddressListTableModel addressesModel = new AddressListTableModel();
                    AddressTableModel resultsModel = new AddressTableModel(); // Fixed typo
                    DatabaseManager.getInstance().initDatabase(); // Use singleton instance
                    List<String> savedAddresses = DatabaseManager.getInstance().loadAddresses(); // Use singleton instance
                    savedAddresses.forEach(addressesModel::addAddress);
                    AddressScannerView view = new AddressScannerView(resultsModel, addressesModel);
                    AddressScannerController controller = new AddressScannerController(
                            model, view, resultsModel, addressesModel);
                    model.addObserver(view);
                    view.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}