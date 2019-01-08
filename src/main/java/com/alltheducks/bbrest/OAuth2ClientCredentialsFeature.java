package com.alltheducks.bbrest;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.function.Function;

public class OAuth2ClientCredentialsFeature implements Feature {

    private final OAuth2ClientRequestFilter requestFilter;
    private final OAuth2ClientResponseFilter responseFilter;

    public OAuth2ClientCredentialsFeature(final String oauthKey,
                                          final String oauthSecret,
                                          final URI accessTokenUri,
                                          final Function<Response, String> errorExtractor) {

        final TokenContext tokenContext = new TokenContext(oauthKey, oauthSecret, accessTokenUri, errorExtractor);

        this.requestFilter = new OAuth2ClientRequestFilter(tokenContext);
        this.responseFilter = new OAuth2ClientResponseFilter(tokenContext);
    }

    public OAuth2ClientCredentialsFeature(final String oauthKey, final String oauthSecret, final URI accessTokenUri) {
        this(oauthKey, oauthSecret, accessTokenUri, (r) -> {
            final RestError error = r.readEntity(RestError.class);
            return error.getErrorDescription();
        });
    }

    @Override
    public boolean configure(final FeatureContext context) {
        context.register(requestFilter);
        context.register(responseFilter);
        return true;
    }

}
