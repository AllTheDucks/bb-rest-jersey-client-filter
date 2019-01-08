package com.alltheducks.bbrest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class TokenResponse {
    @JsonIgnore
    private long tokenLastRefreshedTime;
    private String accessToken;
    private String tokenType;
    private int expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty("token_type")
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty("expires_in")
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }


    public long getTokenLastRefreshedTime() {
        return tokenLastRefreshedTime;
    }

    public void setTokenLastRefreshedTime(long tokenLastRefreshedTime) {
        this.tokenLastRefreshedTime = tokenLastRefreshedTime;
    }

    public boolean hasExpired() {
        return tokenLastRefreshedTime + expiresIn < System.currentTimeMillis() / 1000;
    }

}
