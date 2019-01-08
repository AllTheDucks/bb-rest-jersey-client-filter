package com.alltheducks.bbrest;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OAuth2ClientCredentialsFeature implements Feature {

    private final OAuth2ClientRequestFilter requestFilter;
    private final OAuth2ClientResponseFilter responseFilter;
    private final String basicAuthHeaderValue;
    private TokenResponse token;

    private URI accessTokenUri;

    public OAuth2ClientCredentialsFeature(String oauthKey, String oauthSecret, URI accessTokenUri) {
        this.requestFilter = new OAuth2ClientRequestFilter(this);
        this.responseFilter = new OAuth2ClientResponseFilter(this);
        this.accessTokenUri = accessTokenUri;
        byte[] encodedKeySecret = Base64.getEncoder().encode((oauthKey + ":" + oauthSecret).getBytes(StandardCharsets.UTF_8));

        this.basicAuthHeaderValue = "Basic " + new String(encodedKeySecret, StandardCharsets.UTF_8);

    }

    @Override
    public boolean configure(FeatureContext context) {
        context.register(requestFilter);
        context.register(responseFilter);
        return true;
    }

    public TokenResponse getToken() {
        return token;
    }

    public void setToken(TokenResponse token) {
        this.token = token;
    }


    public TokenResponse fetchAccessToken(ClientRequestContext requestContext) {
        if (token == null || token.hasExpired()) {
            ClientBuilder builder = ClientBuilder.newBuilder();
            builder.sslContext(requestContext.getClient().getSslContext());
            builder.hostnameVerifier(requestContext.getClient().getHostnameVerifier());
            Client c = builder
                    .register(JacksonFeature.class)
                    .build();

            MultivaluedMap<String, String> formdata = new MultivaluedStringMap();
            formdata.putSingle("grant_type", "client_credentials");
            Response r = c.target(this.accessTokenUri)
                    .request()
                    .header("Authorization", basicAuthHeaderValue)
                    .post(Entity.form(formdata));
            if (r.getStatus() == 400) {
                //TODO this whole block is very Bb REST specific.
                RestError error = r.readEntity(RestError.class);
                System.out.println(error.getErrorDescription());
                throw new AuthenticationFailureException("OAuth Client Credential Authentication failed: " + error.getErrorDescription());
            } else {
                token = r.readEntity(TokenResponse.class);
                token.setTokenLastRefreshedTime(System.currentTimeMillis() / 1000);
            }
        }
        return token;
    }

}
