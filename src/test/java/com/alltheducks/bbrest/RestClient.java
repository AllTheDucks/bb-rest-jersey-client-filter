package com.alltheducks.bbrest;

import com.alltheducks.bbrest.jersey.OAuth2ClientCredentialsFeature;
import com.alltheducks.bbrest.paging.PagingStreams;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by wiley on 29/08/2016.
 */
public class RestClient {

    private static final int PAGE_SIZE = 10;
    private static final GenericType<Map<String, Object>> RESPONSE_TYPE = new GenericType<Map<String, Object>>() {
    };

    public static void main(String[] args) throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException {
        ClientBuilder builder = getUnsafeSSLClientBuilder();

        String oauthKey = args[0];
        String oauthSecret = args[1];

        Client tokenClient = builder
                .register(JacksonFeature.class)
                .register(HttpAuthenticationFeature.basic(oauthKey, oauthSecret))
                .build();

        Client c = builder
                .register(JacksonFeature.class)
                .register(new OAuth2ClientCredentialsFeature(
                        new URI("https://localhost:9877/learn/api/public/v1/oauth2/token"),
                        tokenClient
                ))
                .build();

        MultivaluedMap<String, String> formdata = new MultivaluedStringMap();
        Response r = c.target("https://localhost:9877/learn/api/public/v1/courses/_1_1")
                .request()
                .get();

        String responseBody = r.readEntity(String.class);

//        printHeaders(r);
        System.out.println(responseBody);
        System.out.println();

        r = c.target("https://localhost:9877/learn/api/public/v1/courses/_1_1")
                .request()
                .get();

        responseBody = r.readEntity(String.class);

//        printHeaders(r);
        System.out.println(responseBody);
        System.out.println();

        final Stream<Map<String, Object>> stream = PagingStreams.getStream((page) ->
                c.target("https://localhost:9877")
                .path("learn/api/public/v1/users/{userId}/courses")
                .queryParam("offset", page * PAGE_SIZE)
                .queryParam("limit", PAGE_SIZE)
                .resolveTemplate("userId", "userName:sargo")
                .request()
                .get(), RESPONSE_TYPE);
        stream.forEachOrdered(System.out::println);
        System.out.println();

        final Stream<Map<String, Object>> stream2 = PagingStreams.getStream((page) -> c.target("https://localhost:9877")
                .path("learn/api/public/v1/courses")
                .queryParam("offset", page * PAGE_SIZE)
                .queryParam("limit", PAGE_SIZE)
//                .queryParam("courseId", "externalId:LAWS10-102_2018_JAN_STD_01")
                .queryParam("fields", "courseId")
                .request()
                .get(), RESPONSE_TYPE);
        stream2.forEachOrdered(System.out::println);
        System.out.println();

        final Stream<Map<String, Object>> stream3 = PagingStreams.getStream((page) -> c.target("https://localhost:9877")
                .path("learn/api/public/v2/courses/{courseId}/gradebook/columns")
                .queryParam("offset", page * PAGE_SIZE)
                .queryParam("limit", PAGE_SIZE)
                .resolveTemplate("courseId", "externalId:LAWS10-102_2018_JAN_STD_01")
//                .queryParam("fields", "id,score.possible")
                .request()
                .get(), RESPONSE_TYPE);
        stream3.forEachOrdered(System.out::println);
        System.out.println();

        final Stream<Map<String, Object>> stream4 = PagingStreams.getStream((page) -> c.target("https://localhost:9877")
                .path("learn/api/public/v2/courses/{courseId}/gradebook/columns/{columnId}/users")
                .queryParam("offset", page * PAGE_SIZE)
                .queryParam("limit", PAGE_SIZE)
                .resolveTemplate("courseId", "externalId:LAWS10-102_2018_JAN_STD_01")
                .resolveTemplate("columnId", "_57_1")
                .request()
                .get(), RESPONSE_TYPE);
        stream4.forEachOrdered(System.out::println);
        System.out.println();

        final Stream<Map<String, Object>> stream5 = PagingStreams.getStream((page) -> c.target("https://localhost:9877")
                .path("learn/api/public/v2/courses/{courseId}/gradebook/columns/{columnId}/users")
                .queryParam("offset", page * PAGE_SIZE)
                .queryParam("limit", PAGE_SIZE)
                .resolveTemplate("courseId", "externalId:LAWS10-102_2018_JAN_STD_01")
                .resolveTemplate("columnId", "_57_1")
                .request()
                .get(), RESPONSE_TYPE);
        stream5.forEachOrdered(System.out::println);
        System.out.println();

        final Stream<Map<String, Object>> stream6 = PagingStreams.getStream((page) ->
                c.target("https://localhost:9877")
                            .path("learn/api/public/v1/courses/{courseId}/users")
                        .queryParam("offset", page * PAGE_SIZE)
                        .queryParam("limit", PAGE_SIZE)
                        .queryParam("expand", "user") //Since: 3400.7.0
                        .queryParam("fields", "userId,user.externalId")
                        .resolveTemplate("courseId", "externalId:LAWS10-102_2018_JAN_STD_01")
                        .request()
                        .get(), RESPONSE_TYPE);
        stream6.forEachOrdered(System.out::println);
        System.out.println();

    }

    private static void printHeaders(Response r) {
        final Set<Map.Entry<String, List<Object>>> entries = r.getHeaders().entrySet();
        for (Map.Entry<String, List<Object>> entry : entries) {
            for (Object value : entry.getValue()) {
                System.out.print(entry.getKey());
                System.out.print(": ");
                System.out.print(value);
                System.out.println();
            }
        }

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
