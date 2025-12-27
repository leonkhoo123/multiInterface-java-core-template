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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class HttpLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

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

        long startTime = 0;

        try {
            chain.doFilter(cachedRequest, cachedResponse);
            startTime = logRequest(requestId,cachedRequest);
        } catch (Exception e) {
            log.error("Response error: {} {}", cachedRequest.getMethod(), cachedRequest.getRequestURI(), e);
            throw e;
        } finally {
            logResponse(requestId,cachedRequest, cachedResponse, startTime);
            cachedResponse.copyBodyToResponse(); // important: copy response back
        }
    }

    private long logRequest(String requestId, ContentCachingRequestWrapper request) throws IOException {
        String body = new String(request.getContentAsByteArray(), request.getCharacterEncoding());
        long startTime = System.currentTimeMillis();
        log.debug("\n[{}] REQUEST: {} [{}] \nX-Request-ID: {} \nHeaders:\n{} \nBody: {}",
                sdf.format(new Date(startTime)),
                request.getMethod(),
                request.getRequestURL(),
                requestId,
                getHeaders(request),
                body.isEmpty() ? "{}" : body);
        return startTime;
    }

    private void logResponse(String requestId, ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long startTime) throws IOException {
        String body = new String(response.getContentAsByteArray(), response.getCharacterEncoding());
        long endTime = System.currentTimeMillis();
        long elapsed = endTime - startTime;
        log.debug("\n[{}] RESPONSE: {} [{}][status={}][{}ms] \nHeaders:\n{} \nBody: {}",
                sdf.format(new Date(endTime)),
                request.getMethod(),
                request.getRequestURL(),
                response.getStatus(),
                elapsed,
                getHeaders(response),
                body.isEmpty() ? "{}" : body);
        log.info("[{}] {} {} completed, status={} [{}ms]", requestId ,request.getMethod(), request.getRequestURI(), response.getStatus(),elapsed);
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
