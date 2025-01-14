package com.bitcoinchecker.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;


/**
 * Response DTO for ChainAbuse API address lookups.
 * Features:
 * - Stores list of abuse reports
 * - Inner classes for report details and addresses
 * - JSON deserialization with ignored unknown fields
 * - Report counting functionality
 * Acts as container DTO between API and app layers.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressResponse {
    private List<ChainAbuseReport> reports = new ArrayList<>();

    public void setReports(List<ChainAbuseReport> reports) {
        this.reports = reports;
    }

    public int getTotalReports() {
        return reports.size();
    }
}