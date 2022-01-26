package com.alltheducks.bbrest.jersey.cache;

import com.alltheducks.bbrest.jersey.model.Token;

import java.time.Clock;
import java.util.Optional;

public class InMemoryTokenCache extends ExpiringTokenCache {

    private Token token = null;

    public InMemoryTokenCache(final Clock clock) {
        super(clock);
    }

    @Override
    public void clearToken() {
        this.token = null;
    }

    @Override
    public void cacheToken(Token token) {
        this.token = token;
    }

    @Override
    protected Optional<Token> getMaybeExpiredToken() {
        return Optional.ofNullable(token);
    }

}
