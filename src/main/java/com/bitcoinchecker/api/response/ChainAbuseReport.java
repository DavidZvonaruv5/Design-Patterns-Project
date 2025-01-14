
package com.bitcoinchecker.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * DTO(Data Transfer Object) for ChainAbuse API response.
 * Maps JSON to Java object using Jackson annotations.
 * Contains abuse report details:
 * - Report ID
 * - Address
 * - Abuse type
 * - Report count and URL
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChainAbuseReport {
    @JsonProperty("address")
    private String address;

    @JsonProperty("report_count")
    private int reportCount;

    public String getAddress() {
        return address;
    }
}