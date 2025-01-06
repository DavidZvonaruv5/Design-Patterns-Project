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

        for (BitcoinAddress address : addresses) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    AddressResponse response = apiClient.checkAddress(address.getAddress());
                    address.setAbuseCount(response.getTotalReports());

                    // Build report URL
                    String reportUrl = String.format("https://www.chainabuse.com/reports/%s", address.getAddress());
                    address.setReportUrl(reportUrl);

                    notifyAddressScanned(address);
                } catch (Exception e) {
                    logger.log("Error scanning address " + address.getAddress() + ": " + e.getMessage());
                }
            }, executorService);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(this::notifyCompleted)
                .exceptionally(throwable -> {
                    notifyFailed(new Exception("Scan failed", throwable));
                    return null;
                });
    }

    private void notifyStarted() {
        logger.log("scan started");
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
        logger.log("scan completed successfully");
        for (ScanObserver observer : observers) {
            observer.onScanCompleted(addresses);
        }
    }

    private void notifyFailed(Exception e) {
        logger.log("scan failed. Exception: " + e.getMessage());
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