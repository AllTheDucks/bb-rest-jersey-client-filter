package com.alltheducks.bbrest.jersey;

import com.alltheducks.bbrest.jersey.model.Token;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class OAuth2ClientRequestFilter implements ClientRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(OAuth2ClientRequestFilter.class);

    private final TokenContext tokenContext;

    public OAuth2ClientRequestFilter(final TokenContext tokenContext) {
        this.tokenContext = tokenContext;
    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        final Optional<Token> token = this.tokenContext.fetchAccessToken(requestContext);
        if(token.isPresent()) {
            requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + token.get().getAccessToken());
        } else {
            logger.warn("No token found, therefore the request with be sent without the Authorization header.");
        }
    }


}
