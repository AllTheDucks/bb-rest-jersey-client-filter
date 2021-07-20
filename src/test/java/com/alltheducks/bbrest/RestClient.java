package com.alltheducks.bbrest;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.net.ssl.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by wiley on 29/08/2016.
 */
public class RestClient {
    private static final GenericType<Map<String, Object>> RESPONSE_TYPE = new GenericType<Map<String, Object>>() {
    };

    public static void main(String[] args) throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException {
        ClientBuilder builder = getUnsafeSSLClientBuilder();

        String baseUrl = args[0];
        String oauthKey = args[1];
        String oauthSecret = args[2];

        Client c = builder
                .register(JacksonFeature.class)
                .register(new OAuth2ClientCredentialsFeature(
                        oauthKey,
                        oauthSecret,
                        new URI(baseUrl + "/learn/api/public/v1/oauth2/token")
                ))
                .build();

        Response r = c.target(baseUrl + "/learn/api/public/v1/courses/_1_1")
                .request()
                .get();

        String responseBody = r.readEntity(String.class);
        System.out.println(responseBody);

        r = c.target(baseUrl + "/learn/api/public/v1/courses/_1_1")
                .request()
                .get();

        responseBody = r.readEntity(String.class);
        System.out.println(responseBody);
    }



    private static ClientBuilder getUnsafeSSLClientBuilder() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            public X509Certificate[] getAcceptedIssuers(){return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType){}
            public void checkServerTrusted(X509Certificate[] certs, String authType){}
        }};

        // Install the all-trusting trust manager
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
        } catch (java.security.GeneralSecurityException e) {
            throw new RuntimeException(e);
        }


        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        ClientBuilder builder = ClientBuilder.newBuilder();
        builder.sslContext(sc);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        return builder;
    }


}
