package com.audition.configuration;

import com.audition.common.logging.AuditionLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * Interceptor to log HTTP request and response details.
 */
@Component
@NoArgsConstructor
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
    private final AuditionLogger logger = new AuditionLogger();

    /**
     * Logs request and response during HTTP calls.
     *
     * @param request   the HTTP request
     * @param body      request body
     * @param execution request execution chain
     * @return HTTP response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
        final ClientHttpRequestExecution execution) throws IOException {
        final ClientHttpResponse response = execution.execute(request, body);
        logReqRes(request, body, response);
        return response;
    }

    /**
     * Logs request method, URI, request body, and response body.
     *
     * @param request  the HTTP request
     * @param body     request body
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs
     */
    private void logReqRes(final HttpRequest request, final byte[] body, final ClientHttpResponse response)
        throws IOException {
        if (LOG.isInfoEnabled()) {
            logger.info(LOG, "Method: {}", request.getMethod());
            logger.info(LOG, "URI: {}", request.getURI());
            logger.info(LOG, "Request Body: {}", new String(body, StandardCharsets.UTF_8));

            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                final String responseBody = reader.lines().collect(Collectors.joining("\n"));
                logger.info(LOG, "Response Body: {}", responseBody);
            }
        }
    }
}
