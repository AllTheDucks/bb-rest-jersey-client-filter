package com.alltheducks.bbrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

public class OAuth2ClientResponseFilter implements ClientResponseFilter {

    private final Logger logger = LoggerFactory.getLogger(OAuth2ClientResponseFilter.class);

    private static final String TOKEN_RETRY_REQUEST_PROPERTY_KEY = "tokenretryrequest";

    private final TokenContext tokenContext;

    public OAuth2ClientResponseFilter(final TokenContext tokenContext) {
        this.tokenContext = tokenContext;
    }

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) throws IOException {
        final Boolean retryRequestProperty = (Boolean) requestContext.getProperty(TOKEN_RETRY_REQUEST_PROPERTY_KEY);
        final boolean isRetryRequest = retryRequestProperty != null && retryRequestProperty;

        if (responseContext.getStatus() == 401 && !isRetryRequest) {
            logger.debug("Set token to null, and re-request...");
            this.tokenContext.clearToken();
            this.tokenContext.fetchAccessToken(requestContext);

            final Client c = requestContext.getClient();

            final Response r = c.target(requestContext.getUri())
                    .request(responseContext.getMediaType())
                    .property(TOKEN_RETRY_REQUEST_PROPERTY_KEY, true)
                    .build(requestContext.getMethod()).invoke();

            final InputStream entityStream = r.readEntity(InputStream.class);

            responseContext.setEntityStream(entityStream);
        }
    }
}
