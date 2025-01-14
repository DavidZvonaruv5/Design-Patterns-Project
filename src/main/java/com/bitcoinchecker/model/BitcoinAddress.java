package com.bitcoinchecker.model;

public class BitcoinAddress {
    private String address;
    private int abuseCount;
    private String reportUrl;


    /**
     * Model class representing a Bitcoin address with abuse data.
     * Stores:
     * - Address string
     * - Number of abuse reports
     * - URL to detailed report
     */
    public BitcoinAddress(String address) {
        this.address = address;
        this.abuseCount = 0;
        this.reportUrl = "";
    }

    // Getters and setters
    public String getAddress() {
        return address;
    }

    public int getAbuseCount() {
        return abuseCount;
    }

    public void setAbuseCount(int abuseCount) {
        this.abuseCount = abuseCount;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    @Override
    public String toString() {
        return "BitcoinAddress{" +
                "address='" + address + '\'' +
                ", abuseCount=" + abuseCount +
                ", reportUrl='" + reportUrl + '\'' +
                '}';
    }
}