package com.alltheducks.bbrest.jersey.model;

import java.time.Instant;

public class Token {
    private final String accessToken;
    private final Instant expiry;

    public Token(String accessToken, Instant expiry) {
        this.accessToken = accessToken;
        this.expiry = expiry;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Instant getExpiry() {
        return expiry;
    }
}
