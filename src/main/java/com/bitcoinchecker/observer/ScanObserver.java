package com.bitcoinchecker.observer;

import com.bitcoinchecker.model.BitcoinAddress;
import java.util.List;

public interface ScanObserver {
    void onScanStarted();
    void onAddressScanned(BitcoinAddress address);
    void onScanCompleted(List<BitcoinAddress> results);
    void onScanFailed(Exception e);
}