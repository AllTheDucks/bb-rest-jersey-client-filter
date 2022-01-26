package com.alltheducks.bbrest.jersey;

import com.alltheducks.bbrest.jersey.cache.ExpiringTokenCache;
import com.alltheducks.bbrest.jersey.model.Token;
import com.alltheducks.bbrest.jersey.provider.TokenProvider;
import jakarta.ws.rs.client.ClientRequestContext;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TokenContextTest {

    @Test
    public void testFetchAccessToken_whenTokenNotCached_expectTokenFromProvider() {
        final var now = Instant.ofEpochSecond(1641789658);
        final var tokenExpiry = now.plus(5, ChronoUnit.MINUTES);

        final var tokenProvider = mock(TokenProvider.class);
        final var token = new Token("newtoken", tokenExpiry);
        final var tokenCache = mock(ExpiringTokenCache.class);
        final var requestContext = mock(ClientRequestContext.class);

        when(tokenProvider.fetchAccessToken(any()))
                .thenReturn(token);
        when(tokenCache.getToken())
                .thenReturn(Optional.empty());

        final var tokenContext = new TokenContext(tokenProvider, tokenCache);

        final var returnedToken = tokenContext.fetchAccessToken(requestContext);

        assertTrue(returnedToken.isPresent());
        assertSame(token, returnedToken.get());
    }

    @Test
    public void testFetchAccessToken_whenTokenCachedAndNotExpired_expectTokenFromCache() {
        final var now = Instant.ofEpochSecond(1641789658);
        final var tokenExpiry = now.plus(5, ChronoUnit.MINUTES);

        final var tokenProvider = mock(TokenProvider.class);
        final var token = new Token("newtoken", tokenExpiry);
        final var tokenCache = mock(ExpiringTokenCache.class);
        final var requestContext = mock(ClientRequestContext.class);

        when(tokenCache.getToken())
                .thenReturn(Optional.of(token));

        final var tokenContext = new TokenContext(tokenProvider, tokenCache);

        final var returnedToken = tokenContext.fetchAccessToken(requestContext);

        assertTrue(returnedToken.isPresent());
        assertSame(token, returnedToken.get());
    }

}