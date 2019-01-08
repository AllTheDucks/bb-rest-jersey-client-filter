package com.alltheducks.bbrest;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.function.Function;

public class OAuth2ClientCredentialsFeature implements Feature {

    private final String oauthKey;
    private final String oauthSecret;
    private final URI accessTokenUri;
    private final Function<Response, String> errorExtractor;

    public OAuth2ClientCredentialsFeature(String oauthKey, String oauthSecret, URI accessTokenUri, Function<Response, String> errorExtractor) {
        this.oauthKey = oauthKey;
        this.oauthSecret = oauthSecret;
        this.accessTokenUri = accessTokenUri;
        this.errorExtractor = errorExtractor;
    }

    public OAuth2ClientCredentialsFeature(final String oauthKey, final String oauthSecret, final URI accessTokenUri) {
        this(oauthKey, oauthSecret, accessTokenUri, (r) -> {
            final RestError error = r.readEntity(RestError.class);
            return error.getErrorDescription();
        });
    }

    @Override
    public boolean configure(final FeatureContext context) {
        final TokenContext tokenContext = new TokenContext(this.oauthKey, this.oauthSecret, this.accessTokenUri, this.errorExtractor);

        context.register(new OAuth2ClientRequestFilter(tokenContext));
        context.register(new OAuth2ClientResponseFilter(tokenContext));

        return true;
    }

}
