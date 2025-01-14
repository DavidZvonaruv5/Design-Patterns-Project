package com.bitcoinchecker.observer;

import com.bitcoinchecker.model.BitcoinAddress;
import java.util.List;

/**
 * Observer interface for address scanning events.
 * Defines contract for components interested in scan status updates:
 * - Start/completion notifications
 * - Per-address scan results
 * - Error handling
 */
public interface ScanObserver {
    void onScanStarted();
    void onAddressScanned(BitcoinAddress address);
    void onScanCompleted(List<BitcoinAddress> results);
    void onScanFailed(Exception e);
}