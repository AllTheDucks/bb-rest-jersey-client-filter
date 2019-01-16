package com.alltheducks.bbrest.jersey;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.Clock;
import java.util.function.Function;

public class TokenContext {

    private final Logger logger = LoggerFactory.getLogger(TokenContext.class);

    private final Client tokenClient;
    private final URI accessTokenUri;
    private final Function<Response, String> errorExtractor;
    private final Clock clock;

    private Token token;
    private long tokenLastRefreshedTime;

    public TokenContext(final URI accessTokenUri,
                        final Client tokenClient,
                        final Function<Response, String> errorExtractor,
                        final Clock clock) {
        this.tokenClient = tokenClient;
        this.errorExtractor = errorExtractor;
        this.accessTokenUri = accessTokenUri;
        this.clock = clock;
    }

    public TokenContext(final URI accessTokenUri,
                        final Client tokenClient,
                        final Function<Response, String> errorExtractor) {
        this(accessTokenUri, tokenClient, errorExtractor, Clock.systemUTC());
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

            final MultivaluedMap<String, String> formData = new MultivaluedStringMap();
            formData.putSingle("grant_type", "client_credentials");

            final Response r = this.tokenClient.target(this.accessTokenUri)
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
