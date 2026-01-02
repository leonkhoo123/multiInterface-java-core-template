package com.leon.rest_api.logger;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Component
public class HttpLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Set<String> SENSITIVE_ENDPOINTS = Set.of("/api/v1/login");

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        var cachedRequest = new BufferedRequestWrapper((HttpServletRequest) request);
        var cachedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        // get request ID from header if present
        String requestId = cachedRequest.getHeader("X-Request-ID");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        cachedResponse.setHeader("X-Request-ID", requestId);

        // Log request BEFORE processing
        long startTime = logRequest(requestId, cachedRequest);

        try {
            chain.doFilter(cachedRequest, cachedResponse);
        } catch (Exception e) {
            log.error("Response error: {} {}", cachedRequest.getMethod(), cachedRequest.getRequestURI(), e);
            throw e;
        } finally {
            logResponse(requestId,cachedRequest, cachedResponse, startTime);
            cachedResponse.copyBodyToResponse(); // important: copy response back
        }
    }

    private long logRequest(String requestId, BufferedRequestWrapper request) throws IOException {
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }

        String body;
        if (SENSITIVE_ENDPOINTS.contains(request.getRequestURI())) {
            body = "[**SENSITIVE CONTENT HIDDEN**]";
        } else {
            body = new String(request.getContentAsByteArray(), encoding);
        }

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

    private void logResponse(String requestId, BufferedRequestWrapper request, ContentCachingResponseWrapper response, long startTime) throws IOException {
        String body;
        if (SENSITIVE_ENDPOINTS.contains(request.getRequestURI())) {
            body = "[**SENSITIVE CONTENT HIDDEN**]";
        } else {
            body = new String(response.getContentAsByteArray(), response.getCharacterEncoding());
        }

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

    /**
     * A wrapper that reads the request body into memory immediately.
     * This allows logging the body BEFORE the chain processes it.
     */
    private static class BufferedRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] body;

        public BufferedRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            this.body = StreamUtils.copyToByteArray(request.getInputStream());
        }

        @Override
        public ServletInputStream getInputStream() {
            return new BufferedServletInputStream(this.body);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            String encoding = getCharacterEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            return new BufferedReader(new InputStreamReader(getInputStream(), encoding));
        }

        public byte[] getContentAsByteArray() {
            return body;
        }
    }

    private static class BufferedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream buffer;

        public BufferedServletInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public int read() {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // Not implemented for blocking IO
        }
    }
}
