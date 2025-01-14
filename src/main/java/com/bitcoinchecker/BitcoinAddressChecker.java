package com.bitcoinchecker;

import com.bitcoinchecker.db.DatabaseManager;
import com.bitcoinchecker.model.AddressListTableModel;
import com.bitcoinchecker.model.AddressScannerModel;
import com.bitcoinchecker.model.AddressTableModel;
import com.bitcoinchecker.model.BitcoinAddress;
import com.bitcoinchecker.view.AddressScannerView;
import com.bitcoinchecker.controller.AddressScannerController;
import com.bitcoinchecker.util.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.List;

/**
 * Main application class for Bitcoin Address Scanner that implements MVC pattern.
 * Initializes the application components:
 * - Models (AddressScannerModel, AddressListTableModel, AddressTableModel)
 * - View (AddressScannerView)
 * - Controller (AddressScannerController)
 * Sets up UI, database connection, and loads saved addresses.
 */
public class BitcoinAddressChecker {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                try {
                    AddressScannerModel model = new AddressScannerModel();
                    AddressListTableModel addressesModel = new AddressListTableModel();
                    AddressTableModel resultsModel = new AddressTableModel();
                    DatabaseManager.getInstance().initDatabase(); // Use singleton instance
                    List<String> savedAddresses = DatabaseManager.getInstance().loadAddresses(); // Use singleton instance
                    List<BitcoinAddress> savedResults = DatabaseManager.getInstance().loadScanResults();
                    savedResults.forEach(resultsModel::addAddress);
                    savedAddresses.forEach(addressesModel::addAddress);
                    AddressScannerView view = new AddressScannerView(resultsModel, addressesModel);
                    Logger.setLogArea(view.getLogArea());
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