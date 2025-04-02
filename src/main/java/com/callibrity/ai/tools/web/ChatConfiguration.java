package com.callibrity.ai.tools.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class ChatConfiguration {
    @Bean
    public RestClientCustomizer restClientCustomizer(HttpClientProperties httpClientProperties) {
        return restClientBuilder -> {
            restClientBuilder.requestInterceptor(new ClientLoggerRequestInterceptor());
        };
    }


    public static class ClientLoggerRequestInterceptor implements ClientHttpRequestInterceptor {
        private static final Logger log = LogManager.getLogger(ClientLoggerRequestInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            logRequest(request, body);
            var response = execution.execute(request, body);
            return logResponse(request, response);
        }

        private void logRequest(HttpRequest request, byte[] body) {
            log.info("Request: {} {}", request.getMethod(), request.getURI());
            log.debug(request.getHeaders());
            if (body != null && body.length > 0) {
                log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
            }
        }

        private ClientHttpResponse logResponse(HttpRequest request,
                                               ClientHttpResponse response) throws IOException {
            log.info("Response status: {}", response.getStatusCode());
            log.debug(response.getHeaders());

            byte[] responseBody = response.getBody().readAllBytes();
            if (responseBody.length > 0) {
                log.info("Response body: {}",
                        new String(responseBody, StandardCharsets.UTF_8));
            }

            // Return wrapped response to allow reading the body again
            return new BufferingClientHttpResponseWrapper(response, responseBody);
        }

    }

    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;
        private final byte[] body;

        public BufferingClientHttpResponseWrapper(ClientHttpResponse response,
                                                  byte[] body) {
            this.response = response;
            this.body = body;
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        // Delegate other methods to wrapped response
        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }

        @Override
        public void close() {
            response.close();
        }

        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }
    }

}
