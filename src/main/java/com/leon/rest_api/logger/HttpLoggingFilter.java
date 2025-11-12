package com.leon.rest_api.logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class HttpLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        var cachedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        var cachedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        // get request ID from header if present
        String requestId = cachedRequest.getHeader("X-Request-ID");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        cachedResponse.setHeader("X-Request-ID", requestId);

        // record start time
        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(cachedRequest, cachedResponse);
        } catch (Exception e) {
            log.error("Response error: {} {}", cachedRequest.getMethod(), cachedRequest.getRequestURI(), e);
            throw e;
        } finally {
            long elapsed = System.currentTimeMillis() - startTime;
            logRequest(requestId,cachedRequest);
            logResponse(cachedRequest, cachedResponse,elapsed);
            cachedResponse.copyBodyToResponse(); // important: copy response back
        }
    }

    private void logRequest(String requestId, ContentCachingRequestWrapper request) throws IOException {
        String body = new String(request.getContentAsByteArray(), request.getCharacterEncoding());
        log.debug("\nReceived: {} {} \n[X-Request-ID: {}] \nHeaders:\n{} \nBody: {}",
                request.getMethod(),
                request.getRequestURL(),
                requestId,
                getHeaders(request),
                body.isEmpty() ? "{}" : body);
    }

    private void logResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long elapsed) throws IOException {
        String body = new String(response.getContentAsByteArray(), response.getCharacterEncoding());
        log.debug("\nResponse: {} {} status={} [{}ms] \nHeaders:\n{} \nBody: {}",
                request.getMethod(),
                request.getRequestURL(),
                response.getStatus(),
                elapsed,
                getHeaders(response),
                body.isEmpty() ? "{}" : body);
        log.info("{} {} completed, status={} [{}ms]", request.getMethod(), request.getRequestURI(), response.getStatus(),elapsed);
    }

    private String getHeaders(HttpServletRequest request) {
        var headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(h -> headers.append(h).append(": ").append(request.getHeader(h)).append("; "));
        return headers.toString();
    }

    private String getHeaders(HttpServletResponse response) {
        var headers = new StringBuilder();
        response.getHeaderNames().forEach(h -> headers.append(h).append(": ").append(response.getHeader(h)).append("; "));
        return headers.toString();
    }
}
