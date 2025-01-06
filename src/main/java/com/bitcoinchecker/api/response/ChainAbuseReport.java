
package com.bitcoinchecker.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChainAbuseReport {
    @JsonProperty("id")
    private String id;

    @JsonProperty("address")
    private String address;

    @JsonProperty("abuse_type")
    private String abuseType;

    @JsonProperty("report_count")
    private int reportCount;

    @JsonProperty("report_url")
    private String reportUrl;

    // Getters
    public String getId() { return id; }
    public String getAddress() { return address; }
    public String getAbuseType() { return abuseType; }
    public int getReportCount() { return reportCount; }
    public String getReportUrl() { return reportUrl; }
}
