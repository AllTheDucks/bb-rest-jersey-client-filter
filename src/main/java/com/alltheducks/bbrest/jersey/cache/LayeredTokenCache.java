package com.alltheducks.bbrest.jersey.cache;

import com.alltheducks.bbrest.jersey.model.Token;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

public class LayeredTokenCache extends ExpiringTokenCache {

    private final List<ExpiringTokenCache> layers;

    public LayeredTokenCache(final Clock clock, final List<ExpiringTokenCache> layers) {
        super(clock);
        this.layers = layers;
    }

    @Override
    public void cacheToken(Token token) {
        for (final ExpiringTokenCache layer : layers) {
            layer.cacheToken(token);
        }
    }

    @Override
    public Optional<Token> getToken() {
        return layers.stream()
                .map(ExpiringTokenCache::getToken)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public void clearToken() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            layers.get(i).clearToken();
        }
    }

    @Override
    protected Optional<Token> getMaybeExpiredToken() {
        // Purposefully not implemented as "getToken" is overridden
        throw new RuntimeException("Not Implemented");
    }

}
