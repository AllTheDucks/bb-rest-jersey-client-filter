package com.alltheducks.bbrest.jersey;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.Clock;
import java.util.function.Function;

public class TokenContext {

    private final Logger logger = LoggerFactory.getLogger(TokenContext.class);

    private final URI accessTokenUri;
    private final String oauthKey;
    private final String oauthSecret;
    private final Function<Response, String> errorExtractor;
    private final Clock clock;

    private Token token;
    private long tokenLastRefreshedTime;

    public TokenContext(final String oauthKey,
                        final String oauthSecret,
                        final URI accessTokenUri,
                        final Function<Response, String> errorExtractor,
                        final Clock clock) {
        this.oauthKey = oauthKey;
        this.oauthSecret = oauthSecret;
        this.accessTokenUri = accessTokenUri;
        this.errorExtractor = errorExtractor;
        this.clock = clock;
    }

    public TokenContext(final String oauthKey,
                        final String oauthSecret,
                        final URI accessTokenUri,
                        final Function<Response, String> errorExtractor) {
        this(oauthKey, oauthSecret, accessTokenUri, errorExtractor, Clock.systemUTC());
    }

    public synchronized void clearToken() {
        this.token = null;
        tokenLastRefreshedTime = 0;
    }

    public Token fetchAccessToken(final ClientRequestContext requestContext) {
        if (token == null || isTokenExpired(token)) {
            return fetchNewAccessToken(requestContext);
        }
        return token;
    }

    private synchronized Token fetchNewAccessToken(final ClientRequestContext requestContext) {
        if (token == null || isTokenExpired(token)) {
            logger.debug("New token required");
            final ClientBuilder builder = ClientBuilder.newBuilder();
            builder.sslContext(requestContext.getClient().getSslContext());
            builder.hostnameVerifier(requestContext.getClient().getHostnameVerifier());
            final Client client = builder
                    .register(JacksonFeature.class)
                    .register(HttpAuthenticationFeature.basic(this.oauthKey, this.oauthSecret))
                    .build();

            final MultivaluedMap<String, String> formData = new MultivaluedStringMap();
            formData.putSingle("grant_type", "client_credentials");

            final Response r = client.target(this.accessTokenUri)
                    .request()
                    .post(Entity.form(formData));

            if (r.getStatus() != 200) {
                final String errorDescription = this.errorExtractor.apply(r);
                logger.error(errorDescription);
                throw new AuthenticationFailureException("OAuth Client Credential Authentication failed: " + errorDescription);
            } else {
                token = r.readEntity(Token.class);
                tokenLastRefreshedTime = clock.millis() / 1000;
            }
        }
        return token;
    }

    private boolean isTokenExpired(final Token token) {
        return tokenLastRefreshedTime + token.getExpiresIn() < clock.millis() / 1000;
    }

}
