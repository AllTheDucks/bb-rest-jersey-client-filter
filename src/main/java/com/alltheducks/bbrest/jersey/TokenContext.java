package com.alltheducks.bbrest.jersey;

import com.alltheducks.bbrest.jersey.cache.ExpiringTokenCache;
import com.alltheducks.bbrest.jersey.cache.InMemoryTokenCache;
import com.alltheducks.bbrest.jersey.model.Token;
import com.alltheducks.bbrest.jersey.provider.TokenProvider;
import jakarta.ws.rs.client.ClientRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.Optional;

public class TokenContext {

    private final Logger logger = LoggerFactory.getLogger(TokenContext.class);

    private final TokenProvider tokenProvider;
    private final ExpiringTokenCache tokenCache;

    public TokenContext(final TokenProvider tokenProvider,
                        final ExpiringTokenCache tokenCache) {
        this.tokenProvider = tokenProvider;
        this.tokenCache = tokenCache != null ? tokenCache : new InMemoryTokenCache(Clock.systemUTC());
    }

    public void clearToken() {
        this.tokenCache.clearToken();
    }

    public Optional<Token> fetchAccessToken(final ClientRequestContext requestContext) {
        final var token = this.tokenCache.getToken();
        if (token.isPresent()) {
            return token;
        }

        return this.fetchNewAccessToken(requestContext);
    }

    private synchronized Optional<Token> fetchNewAccessToken(final ClientRequestContext requestContext) {
        var token = this.tokenCache.getToken();
        if (token.isPresent()) {
            return token;
        }

        logger.debug("New token required");
        final var newToken = this.tokenProvider.fetchAccessToken(requestContext);
        this.tokenCache.cacheToken(newToken);

        return Optional.of(newToken);
    }

}
