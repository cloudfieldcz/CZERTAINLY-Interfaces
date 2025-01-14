package com.czertainly.api;

import com.czertainly.api.exception.*;
import com.czertainly.api.model.connector.ConnectorDto;
import com.czertainly.core.util.AttributeDefinitionUtils;
import com.czertainly.core.util.KeyStoreUtils;
import com.czertainly.api.model.AttributeDefinition;
import com.czertainly.api.model.connector.AuthType;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.*;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiClient.class);

    // Basic auth attribute names
    public static final String ATTRIBUTE_USERNAME = "username";
    public static final String ATTRIBUTE_PASSWORD = "password";

    // Certificate attribute names
    public static final String ATTRIBUTE_KEYSTORE_TYPE = "keyStoreType";
    public static final String ATTRIBUTE_KEYSTORE = "keyStore";
    public static final String ATTRIBUTE_KEYSTORE_PASSWORD = "keyStorePassword";
    public static final String ATTRIBUTE_TRUSTSTORE_TYPE = "trustStoreType";
    public static final String ATTRIBUTE_TRUSTSTORE = "trustStore";
    public static final String ATTRIBUTE_TRUSTSTORE_PASSWORD = "trustStorePassword";

    // API key attribute names
    public static final String ATTRIBUTE_API_KEY_HEADER = "apiKeyHeader";
    public static final String ATTRIBUTE_API_KEY = "apiKey";

    protected WebClient webClient;

    public WebClient.RequestBodyUriSpec prepareRequest(HttpMethod method, AuthType authType, List<AttributeDefinition> authAttributes) {

        WebClient.RequestBodySpec request;

        // for backward compatibility
        if (authType == null) {
            request = webClient.method(method);
            return (WebClient.RequestBodyUriSpec) request;
        }

        switch (authType) {
            case NONE:
                request = webClient.method(method);
                break;
            case BASIC:
                String username = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_USERNAME, authAttributes);
                String password = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_PASSWORD, authAttributes);

                request = webClient
                        .method(method)
                        .headers(h -> h.setBasicAuth(username, password));
                break;
            case CERTIFICATE:
                SslContext sslContext = createSslContext(authAttributes);
                HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
                webClient.mutate().clientConnector(new ReactorClientHttpConnector(httpClient)).build();

                request = webClient.method(method);
                break;
            case API_KEY:
                String apiKeyHeader = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_API_KEY_HEADER, authAttributes);
                String apiKey = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_API_KEY, authAttributes);

                request = webClient
                        .method(method)
                        .headers(h -> h.set(apiKeyHeader, apiKey));
                break;
            case JWT:
                throw new UnsupportedOperationException("JWT is unimplemented");
            default:
                throw new IllegalArgumentException("Unknown auth type " + authType);
        }

        return (WebClient.RequestBodyUriSpec) request;
    }

    private SslContext createSslContext(List<AttributeDefinition> attributes) {
        try {
            KeyManager km = null;
            String keyStoreData = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_KEYSTORE, attributes);
            if (keyStoreData != null && !keyStoreData.isEmpty()) {
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()); //"SunX509"

                String keyStoreType = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_KEYSTORE_TYPE, attributes);
                String keyStorePassword = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_KEYSTORE_PASSWORD, attributes);
                byte[] keyStoreBytes = Base64.getDecoder().decode(keyStoreData);

                kmf.init(KeyStoreUtils.bytes2KeyStore(keyStoreBytes, keyStorePassword, keyStoreType), keyStorePassword.toCharArray());
                km = kmf.getKeyManagers()[0];
            }

            TrustManager tm = null;
            String trustStoreData = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_TRUSTSTORE, attributes);
            if (trustStoreData != null && !trustStoreData.isEmpty()) {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); //"SunX509"

                String trustStoreType = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_TRUSTSTORE_TYPE, attributes);
                String trustStorePassword = AttributeDefinitionUtils.getAttributeValue(ATTRIBUTE_TRUSTSTORE_PASSWORD, attributes);
                byte[] trustStoreBytes = Base64.getDecoder().decode(keyStoreData);

                tmf.init(KeyStoreUtils.bytes2KeyStore(trustStoreBytes, trustStorePassword, trustStoreType));
                tm = tmf.getTrustManagers()[0];
            }

            return SslContextBuilder
                    .forClient()
                    .keyManager(km)
                    .trustManager(tm)
                    .protocols("TLSv1.2")
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize SslContext.", e);
        }
    }

    private static final ParameterizedTypeReference<List<String>> ERROR_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public static WebClient prepareWebClient() {
        return WebClient.builder()
                .filter(ExchangeFilterFunction.ofResponseProcessor(BaseApiClient::handleHttpExceptions))
                .build();
    }

    private static Mono<ClientResponse> handleHttpExceptions(ClientResponse clientResponse) {
        if (HttpStatus.UNPROCESSABLE_ENTITY.equals(clientResponse.statusCode())) {
            return clientResponse.bodyToMono(ERROR_LIST_TYPE_REF).flatMap(body ->
                    Mono.error(new ValidationException(body.stream()
                                    .map(ValidationError::create)
                                    .collect(Collectors.toList())
                            )
                    )
            );
        }
        if (HttpStatus.NOT_FOUND.equals(clientResponse.statusCode())) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new NotFoundException(body)));
        }
        if (clientResponse.statusCode().is4xxClientError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ConnectorClientException(body, clientResponse.statusCode())));
        }
        if (clientResponse.statusCode().is5xxServerError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ConnectorServerException(body, clientResponse.statusCode())));
        }
        return Mono.just(clientResponse);
    }

    public static <T, R> R processRequest(Function<T, R> func, T request, ConnectorDto connector) throws ConnectorException {
        try {
            return func.apply(request);
        } catch (Exception e) {
            Throwable unwrapped = Exceptions.unwrap(e);
            logger.error(unwrapped.getMessage(), unwrapped);

            if (unwrapped instanceof IOException) {
                throw new ConnectorCommunicationException(unwrapped.getMessage(), unwrapped, connector);
            } else if (unwrapped instanceof ConnectorException) {
                ConnectorException ce = (ConnectorException) unwrapped;
                ce.setConnector(connector);
                throw ce;
            } else {
                throw e;
            }
        }
    }
}
