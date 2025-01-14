package com.bitcoinchecker.model;

import com.bitcoinchecker.api.ChainAbuseClient;
import com.bitcoinchecker.api.response.AddressResponse;
import com.bitcoinchecker.observer.ScanObserver;
import com.bitcoinchecker.util.ApiClientManager;
import com.bitcoinchecker.util.Config;
import com.bitcoinchecker.util.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


/**
 * Core model implementing scan logic and Observer pattern.
 * Features:
 * - Concurrent address scanning via ExecutorService
 * - Observer notifications for scan events
 * - File loading capabilities
 * - API client integration
 * - Error handling and logging
 */
public class AddressScannerModel {
    private final List<BitcoinAddress> addresses;
    private final List<ScanObserver> observers;
    private final Logger logger;
    private final ChainAbuseClient apiClient;
    private final ExecutorService executorService;

    public AddressScannerModel() {
        this.addresses = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.logger = Logger.getInstance();
        this.apiClient = ApiClientManager.getInstance().getClient();
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public void addObserver(ScanObserver observer) {
        observers.add(observer);
    }

    public void addAddress(String address) {
        addresses.add(new BitcoinAddress(address));
    }

    public void loadAddressesFromFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    addAddress(line.trim());
                }
            }
        }
    }

    public void scanAddresses() {
        notifyStarted();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicBoolean hasError = new AtomicBoolean(false);

        for (BitcoinAddress address : addresses) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    AddressResponse response = apiClient.checkAddress(address.getAddress());
                    address.setAbuseCount(response.getTotalReports());
                    String reportUrl = String.format("https://www.chainabuse.com/address/%s", address.getAddress());
                    address.setReportUrl(reportUrl);
                    notifyAddressScanned(address);
                } catch (Exception e) {
                    hasError.set(true);
                    notifyFailed(e);
                }
            }, executorService);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    if (!hasError.get()) {
                        notifyCompleted();
                    }
                });
    }

    private void notifyStarted() {
        logger.log("scan started",false);
        for (ScanObserver observer : observers) {
            observer.onScanStarted();
        }
    }

    private void notifyAddressScanned(BitcoinAddress address) {
        for (ScanObserver observer : observers) {
            observer.onAddressScanned(address);
        }
    }

    private void notifyCompleted() {
        logger.log("scan completed successfully",true);
        for (ScanObserver observer : observers) {
            observer.onScanCompleted(addresses);
        }
    }

    private void notifyFailed(Exception e) {
        logger.log("scan failed. Exception: " + e.getMessage(),true);
        for (ScanObserver observer : observers) {
            observer.onScanFailed(e);
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public List<BitcoinAddress> getAddresses() {
        return new ArrayList<>(addresses);
    }

    public void clearAddresses() {
        addresses.clear();
    }
}