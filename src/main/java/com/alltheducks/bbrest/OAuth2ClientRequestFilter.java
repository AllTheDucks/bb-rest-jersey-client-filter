package com.alltheducks.bbrest;

import javax.ws.rs.client.*;
import java.io.IOException;

public class OAuth2ClientRequestFilter implements ClientRequestFilter {

    private final OAuth2ClientCredentialsFeature clientCredentialsFeature;

    public OAuth2ClientRequestFilter(final OAuth2ClientCredentialsFeature clientCredentialsFeature) {

        this.clientCredentialsFeature = clientCredentialsFeature;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {

        TokenResponse token = this.clientCredentialsFeature.fetchAccessToken(requestContext);



        if (requestContext.getProperty("tokenretryrequest") == null) {
            requestContext.getHeaders().putSingle("Authorization", "Bearer " + "not-a-valid-token");
        } else {
            requestContext.getHeaders().putSingle("Authorization", "Bearer " +
                    token.getAccessToken());
        }
    }


}
