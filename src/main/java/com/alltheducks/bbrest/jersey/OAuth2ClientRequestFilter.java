package com.alltheducks.bbrest.jersey;

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
        final Token token = this.tokenContext.fetchAccessToken(requestContext);
        requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());
    }


}
