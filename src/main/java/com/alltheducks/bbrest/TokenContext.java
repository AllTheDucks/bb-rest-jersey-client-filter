package com.alltheducks.bbrest;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.function.Function;

public class TokenContext {

    private final Logger logger = LoggerFactory.getLogger(TokenContext.class);

    private final URI accessTokenUri;
    private final Function<Response, String> errorExtractor;
    private final Feature authFilter;

    private TokenResponse token;

    public TokenContext(final String oauthKey,
                        final String oauthSecret,
                        final URI accessTokenUri,
                        final Function<Response, String> errorExtractor) {

        this.errorExtractor = errorExtractor;
        this.accessTokenUri = accessTokenUri;

        this.authFilter = HttpAuthenticationFeature.basic(oauthKey, oauthSecret);
    }

    public TokenResponse getToken() {
        return token;
    }

    public void setToken(TokenResponse token) {
        this.token = token;
    }

    public TokenResponse fetchAccessToken(final ClientRequestContext requestContext) {
        if (token == null || token.hasExpired()) {
            logger.debug("New token required");

            final Client c = ClientBuilder.newBuilder()
                    .sslContext(requestContext.getClient().getSslContext())
                    .hostnameVerifier(requestContext.getClient().getHostnameVerifier())
                    .register(JacksonFeature.class)
                    .register(this.authFilter)
                    .build();

            final MultivaluedMap<String, String> formData = new MultivaluedStringMap();
            formData.putSingle("grant_type", "client_credentials");

            final Response r = c.target(this.accessTokenUri)
                    .request()
                    .post(Entity.form(formData));

            if (r.getStatus() != 200) {
                final String errorDescription = this.errorExtractor.apply(r);
                logger.error(errorDescription);
                throw new AuthenticationFailureException("OAuth Client Credential Authentication failed: " + errorDescription);
            } else {
                token = r.readEntity(TokenResponse.class);
                token.setTokenLastRefreshedTime(System.currentTimeMillis() / 1000);
            }
        }
        return token;
    }

}
