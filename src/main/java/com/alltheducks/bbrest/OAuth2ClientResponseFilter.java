package com.alltheducks.bbrest;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

public class OAuth2ClientResponseFilter implements ClientResponseFilter {

    private final OAuth2ClientCredentialsFeature clientCredentialsFeature;

    public OAuth2ClientResponseFilter(OAuth2ClientCredentialsFeature clientCredentialsFeature) {
        this.clientCredentialsFeature = clientCredentialsFeature;
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (responseContext.getStatus() == 401) {
            System.out.println("Set token to null, and re-request...");
            clientCredentialsFeature.setToken(null);

            clientCredentialsFeature.fetchAccessToken(requestContext);

            ClientBuilder builder = ClientBuilder.newBuilder();
            builder.sslContext(requestContext.getClient().getSslContext());
            builder.hostnameVerifier(requestContext.getClient().getHostnameVerifier());
            Client c = requestContext.getClient();

            Response r = c.target(requestContext.getUri())
                    .request(responseContext.getMediaType())
                    .property("tokenretryrequest", true)
                    .build(requestContext.getMethod()).invoke();

            InputStream entityStream = r.readEntity(InputStream.class);

            responseContext.setEntityStream(entityStream);
        }
    }
}
