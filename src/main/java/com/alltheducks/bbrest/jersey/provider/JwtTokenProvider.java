package com.alltheducks.bbrest.jersey.provider;

import com.alltheducks.bbrest.jersey.AuthenticationFailureException;
import com.alltheducks.bbrest.jersey.model.ErrorResponse;
import com.alltheducks.bbrest.jersey.model.Token;
import com.alltheducks.bbrest.jersey.model.TokenResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Clock;
import java.time.temporal.ChronoUnit;

public class JwtTokenProvider implements TokenProvider {

    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final URI accessTokenUri;
    private final String oauthKey;
    private final String oauthSecret;
    private final Clock clock;

    public JwtTokenProvider(final URI accessTokenUri,
                            final String oauthKey,
                            final String oauthSecret,
                            final Clock clock) {
        this.accessTokenUri = accessTokenUri;
        this.oauthKey = oauthKey;
        this.oauthSecret = oauthSecret;
        this.clock = clock;
    }

    @Override
    public Token fetchAccessToken(final ClientRequestContext requestContext) {
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
            final ErrorResponse errorResponse = r.readEntity(ErrorResponse.class);
            logger.error(errorResponse.getErrorDescription());
            throw new AuthenticationFailureException("OAuth Client Credential Authentication failed: " + errorResponse.getErrorDescription());
        } else {
            return this.convertResponseToToken(r.readEntity(TokenResponse.class));
        }
    }

    private Token convertResponseToToken(final TokenResponse tokenResponse) {
        return new Token(
                tokenResponse.getAccessToken(),
                this.clock.instant().plus(tokenResponse.getExpiresIn(), ChronoUnit.SECONDS));
    }
}
