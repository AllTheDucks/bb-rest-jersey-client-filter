package com.alltheducks.bbrest.jersey;

import com.alltheducks.bbrest.jersey.cache.ExpiringTokenCache;
import com.alltheducks.bbrest.jersey.cache.InMemoryTokenCache;
import com.alltheducks.bbrest.jersey.provider.JwtTokenProvider;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import java.net.URI;
import java.time.Clock;

public class OAuth2ClientCredentialsFeature implements Feature {

    private final String oauthKey;
    private final String oauthSecret;
    private final URI accessTokenUri;
    private final ExpiringTokenCache tokenCache;
    private final Clock clock;

    public OAuth2ClientCredentialsFeature(
            final String oauthKey,
            final String oauthSecret,
            final URI accessTokenUri,
            final ExpiringTokenCache tokenCache,
            final Clock clock) {
        this.oauthKey = oauthKey;
        this.oauthSecret = oauthSecret;
        this.accessTokenUri = accessTokenUri;
        this.clock = clock != null ? clock : Clock.systemUTC();
        this.tokenCache = tokenCache != null ? tokenCache : new InMemoryTokenCache(this.clock);
    }

    public OAuth2ClientCredentialsFeature(final String oauthKey,
                                          final String oauthSecret,
                                          final URI accessTokenUri,
                                          final ExpiringTokenCache tokenCache) {
        this(oauthKey, oauthSecret, accessTokenUri, tokenCache, null);
    }

    public OAuth2ClientCredentialsFeature(String oauthKey, String oauthSecret, URI accessTokenUri) {
        this(oauthKey, oauthSecret, accessTokenUri, null, null);
    }

    @Override
    public boolean configure(final FeatureContext context) {
        final var tokenProvider = new JwtTokenProvider(this.accessTokenUri, this.oauthKey, this.oauthSecret, this.clock);
        final TokenContext tokenContext = new TokenContext(tokenProvider, this.tokenCache);

        context.register(new OAuth2ClientRequestFilter(tokenContext));
        context.register(new OAuth2ClientResponseFilter(tokenContext));

        return true;
    }

}
