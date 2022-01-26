package com.alltheducks.bbrest.jersey.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
    private String errorDescription;

    public String getErrorDescription() {
        return errorDescription;
    }

    @JsonProperty("error_description")
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
