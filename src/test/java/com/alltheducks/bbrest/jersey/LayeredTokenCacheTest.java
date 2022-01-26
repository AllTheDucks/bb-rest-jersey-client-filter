package com.alltheducks.bbrest.jersey;

import com.alltheducks.bbrest.jersey.cache.ExpiringTokenCache;
import com.alltheducks.bbrest.jersey.cache.LayeredTokenCache;
import com.alltheducks.bbrest.jersey.model.Token;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LayeredTokenCacheTest {

    @Test
    void getToken_tokenInFirst_expectFirstReturns() {
        final var now = Instant.ofEpochSecond(1641789658);
        final var tokenExpiry = now.plus(5, ChronoUnit.MINUTES);
        final var clock = Clock.fixed(now, ZoneOffset.UTC);

        final var token = new Token("a-token", tokenExpiry);

        final var layer1 = mock(ExpiringTokenCache.class);
        when(layer1.getToken()).thenReturn(Optional.of(token));
        final var layer2 = mock(ExpiringTokenCache.class);

        final var layeredTokenCache = new LayeredTokenCache(clock, List.of(layer1, layer2));

        final var result = layeredTokenCache.getToken();

        assertTrue(result.isPresent());
        assertSame(token, result.get());

        verify(layer1, times(1)).getToken();
        verify(layer2, never()).getToken();
    }

    @Test
    void getToken_tokenInSecond_expectSecondReturns() {
        final var now = Instant.ofEpochSecond(1641789658);
        final var tokenExpiry = now.plus(5, ChronoUnit.MINUTES);
        final var clock = Clock.fixed(now, ZoneOffset.UTC);

        final var token = new Token("a-token", tokenExpiry);

        final var layer1 = mock(ExpiringTokenCache.class);
        final var layer2 = mock(ExpiringTokenCache.class);

        when(layer1.getToken()).thenReturn(Optional.empty());
        when(layer2.getToken()).thenReturn(Optional.of(token));

        final var layeredTokenCache = new LayeredTokenCache(clock, List.of(layer1, layer2));

        final var result = layeredTokenCache.getToken();

        assertTrue(result.isPresent());
        assertSame(token, result.get());

        verify(layer1, times(1)).getToken();
        verify(layer2, times(1)).getToken();
    }

    @Test
    void cacheToken_expectTokenPassedToAllLayers() {
        final var now = Instant.ofEpochSecond(1641789658);
        final var tokenExpiry = now.plus(5, ChronoUnit.MINUTES);
        final var clock = Clock.fixed(now, ZoneOffset.UTC);

        final var token = new Token("a-token", tokenExpiry);

        final var layer1 = mock(ExpiringTokenCache.class);
        final var layer2 = mock(ExpiringTokenCache.class);

        final var layeredTokenCache = new LayeredTokenCache(clock, List.of(layer1, layer2));

        layeredTokenCache.cacheToken(token);

        verify(layer1, times(1)).cacheToken(token);
        verify(layer2, times(1)).cacheToken(token);
    }

    @Test
    void clearToken_expectTokenClearedFromAllLayers() {
        final var now = Instant.ofEpochSecond(1641789658);
        final var tokenExpiry = now.plus(5, ChronoUnit.MINUTES);
        final var clock = Clock.fixed(now, ZoneOffset.UTC);

        final var token = new Token("a-token", tokenExpiry);

        final var layer1 = mock(ExpiringTokenCache.class);
        final var layer2 = mock(ExpiringTokenCache.class);

        final var layeredTokenCache = new LayeredTokenCache(clock, List.of(layer1, layer2));

        layeredTokenCache.clearToken();

        verify(layer1, times(1)).clearToken();
        verify(layer2, times(1)).clearToken();
    }

}