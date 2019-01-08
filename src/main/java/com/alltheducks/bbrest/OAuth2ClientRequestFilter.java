package com.alltheducks.bbrest;

import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

public class OAuth2ClientRequestFilter implements ClientRequestFilter {

    private final TokenContext tokenContext;

    public OAuth2ClientRequestFilter(final TokenContext tokenContext) {
        this.tokenContext = tokenContext;
    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {

        final TokenResponse token = this.tokenContext.fetchAccessToken(requestContext);

        if (requestContext.getProperty(TokenContext.TOKEN_RETRY_REQUEST_PROPERTY_KEY) == null) {
            requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + "not-a-valid-token");
        } else {
            requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " +
                    token.getAccessToken());
        }
    }


}
