package com.alltheducks.bbrest.jersey;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.function.Function;

public class OAuth2ClientCredentialsFeature implements Feature {

    private final URI accessTokenUri;
    private final Client tokenClient;
    private final Function<Response, String> errorExtractor;

    public OAuth2ClientCredentialsFeature(
            final String oauthKey,
            final String oauthSecret,
            final URI accessTokenUri) {
        this(oauthKey, oauthSecret, accessTokenUri, OAuth2ClientCredentialsFeature::defaultErrorExtractor);
    }

    public OAuth2ClientCredentialsFeature(
            final String oauthKey,
            final String oauthSecret,
            final URI accessTokenUri,
            final Function<Response, String> errorExtractor) {
        this(accessTokenUri, createDefaultTokenClient(oauthKey, oauthSecret), errorExtractor);
    }

    public OAuth2ClientCredentialsFeature(
            final URI accessTokenUri,
            final Client tokenClient,
            final Function<Response, String> errorExtractor) {
        this.accessTokenUri = accessTokenUri;
        this.tokenClient = tokenClient;
        this.errorExtractor = errorExtractor;
    }

    public OAuth2ClientCredentialsFeature(
            final URI accessTokenUri,
            final Client tokenClient) {
        this(accessTokenUri, tokenClient, OAuth2ClientCredentialsFeature::defaultErrorExtractor);
    }

    @Override
    public boolean configure(final FeatureContext context) {
        final TokenContext tokenContext = new TokenContext(this.accessTokenUri, this.tokenClient, this.errorExtractor);

        context.register(new OAuth2ClientRequestFilter(tokenContext));
        context.register(new OAuth2ClientResponseFilter(tokenContext));

        return true;
    }

    private static Client createDefaultTokenClient(final String oauthKey, final String oauthSecret) {
        return ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .register(HttpAuthenticationFeature.basic(oauthKey, oauthSecret))
                .build();
    }

    private static String defaultErrorExtractor(Response r) {
        final RestError error = r.readEntity(RestError.class);
        return error.getErrorDescription();
    }

}
