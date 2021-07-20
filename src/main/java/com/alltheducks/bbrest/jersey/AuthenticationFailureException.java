package com.alltheducks.bbrest.jersey;

public class AuthenticationFailureException extends RuntimeException {

    public AuthenticationFailureException() {
        super();
    }

    public AuthenticationFailureException(String message) {
        super(message);
    }

    public AuthenticationFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationFailureException(Throwable cause) {
        super(cause);
    }

    protected AuthenticationFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
