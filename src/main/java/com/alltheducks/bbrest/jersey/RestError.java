package com.alltheducks.bbrest.jersey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestError {
    private String error;
    private String errorDescription;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    @JsonProperty("error_description")
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
