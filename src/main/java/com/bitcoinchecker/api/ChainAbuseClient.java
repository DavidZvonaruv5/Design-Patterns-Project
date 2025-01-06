package com.bitcoinchecker.api;

import com.bitcoinchecker.api.response.AddressResponse;
import com.bitcoinchecker.api.response.ChainAbuseReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

public class ChainAbuseClient {
    private static final String API_BASE_URL = "https://api.chainabuse.com/v0";
    final HttpClient httpClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public ChainAbuseClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public AddressResponse checkAddress(String address) throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/reports?address=" + address))
                    .header("authorization", "Basic Y2FfYTFGalpsRTRjSEEyTVdkb2VUbFpVMUJrUVhnelUyaHBMbXM0VkVrMUsxa3pRVlF2YjFGNFJXNXFNbXRPWjJjOVBROmNhX2ExRmpabEU0Y0hBMk1XZG9lVGxaVTFCa1FYZ3pVMmhwTG1zNFZFazFLMWt6UVZRdmIxRjRSVzVxTW10T1oyYzlQUQ==")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Raw response: " + response.body());
                List<ChainAbuseReport> reports = objectMapper.readValue(response.body(), new TypeReference<List<ChainAbuseReport>>() {});
                AddressResponse addressResponse = new AddressResponse();
                addressResponse.setReports(reports);
                return addressResponse;
            }else if (response.statusCode() == 404) {
                // Address not found in database, return empty response
                AddressResponse emptyResponse = new AddressResponse();
                return emptyResponse;
            } else {
                throw new IOException("API request failed with status code: " +
                        response.statusCode() + ", body: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Error checking address " + address + ": " + e.getMessage());
            throw e;
        }
    }
}