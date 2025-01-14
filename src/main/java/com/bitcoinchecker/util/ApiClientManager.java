package com.bitcoinchecker.util;

import com.bitcoinchecker.api.ChainAbuseClient;


/**
 * Singleton manager for ChainAbuse API client.
 * Provides centralized access to API client instance.
 * Uses static initialization holder pattern for thread-safe singleton.
 */
public class ApiClientManager {
    private ApiClientManager() {
        client = new ChainAbuseClient(Config.getInstance().getApiKey());
    }

    private static class InstanceHolder {
        private static final ApiClientManager INSTANCE = new ApiClientManager();
    }

    private final ChainAbuseClient client;

    public static ApiClientManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public ChainAbuseClient getClient() {
        return client;
    }
}