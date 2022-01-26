package com.alltheducks.bbrest.jersey;

import com.alltheducks.bbrest.jersey.cache.ExpiringTokenCache;
import com.alltheducks.bbrest.jersey.model.Token;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExpiringTokenCacheTest {

    @Test
    void getToken_withExpiredToken_expectEmpty() {
        final var now = Instant.ofEpochSecond(1641789658);
        final var tokenExpiry = now.minus(5, ChronoUnit.MINUTES);
        final var clock = Clock.fixed(now, ZoneOffset.UTC);

        final var token = new Token("a-token", tokenExpiry);

        final var expiringTokenCache = new StaticExpiringTokenCache(clock, token);

        final var result = expiringTokenCache.getToken();

        assertTrue(result.isEmpty());
    }

    @Test
    void getToken_withFreshToken_expectEmpty() {
        final var now = Instant.ofEpochSecond(1641789658);
        final var tokenExpiry = now.plus(5, ChronoUnit.MINUTES);
        final var clock = Clock.fixed(now, ZoneOffset.UTC);

        final var token = new Token("a-token", tokenExpiry);

        final var expiringTokenCache = new StaticExpiringTokenCache(clock, token);

        final var result = expiringTokenCache.getToken();

        assertTrue(result.isPresent());
    }

    private static class StaticExpiringTokenCache extends ExpiringTokenCache {

        private final Token token;

        public StaticExpiringTokenCache(final Clock clock, final Token token) {
            super(clock);
            this.token = token;
        }

        @Override
        public void cacheToken(Token token) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public void clearToken() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        protected Optional<Token> getMaybeExpiredToken() {
            return Optional.ofNullable(this.token);
        }

    }


}