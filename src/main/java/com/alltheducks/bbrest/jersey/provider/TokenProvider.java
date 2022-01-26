package com.alltheducks.bbrest.jersey.provider;

import com.alltheducks.bbrest.jersey.model.Token;
import jakarta.ws.rs.client.ClientRequestContext;

public interface TokenProvider {

    Token fetchAccessToken(ClientRequestContext requestContext);

}
