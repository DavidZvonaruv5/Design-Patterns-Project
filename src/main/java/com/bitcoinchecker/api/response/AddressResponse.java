package com.bitcoinchecker.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressResponse {
    private List<ChainAbuseReport> reports = new ArrayList<>();

    public void setReports(List<ChainAbuseReport> reports) {
        this.reports = reports;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Report {
        private String id;
        private String createdAt;
        private String scamCategory;
        private List<Address> addresses;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Address {
            private String address;
            private String chain;
        }
    }

    public int getTotalReports() {
        return reports.size();
    }
}