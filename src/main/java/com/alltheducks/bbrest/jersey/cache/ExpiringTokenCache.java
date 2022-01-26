package com.alltheducks.bbrest.jersey.cache;

import com.alltheducks.bbrest.jersey.model.Token;

import java.time.Clock;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class ExpiringTokenCache {

    private final Clock clock;

    public ExpiringTokenCache(final Clock clock) {
        this.clock = clock;
    }

    public abstract void cacheToken(Token token);
    public abstract void clearToken();
    protected abstract Optional<Token> getMaybeExpiredToken();

    public Optional<Token> getToken() {
        return this.getMaybeExpiredToken()
                .filter(Predicate.not(this::isTokenExpired));
    }

    private boolean isTokenExpired(final Token token) {
        return this.clock.instant().isAfter(token.getExpiry());
    }

}
