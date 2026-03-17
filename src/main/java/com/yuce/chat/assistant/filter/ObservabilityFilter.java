package com.yuce.chat.assistant.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Ensures every request has request context in MDC for structured logging.
 * traceId/spanId are populated by Micrometer Tracing (OpenTelemetry bridge).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ObservabilityFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String MDC_REQUEST_PATH = "requestPath";
    public static final String MDC_REQUEST_METHOD = "requestMethod";
    public static final String MDC_CORRELATION_ID = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String path = request.getRequestURI();
            String method = request.getMethod();
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            MDC.put(MDC_REQUEST_PATH, path);
            MDC.put(MDC_REQUEST_METHOD, method);
            MDC.put(MDC_CORRELATION_ID, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_REQUEST_PATH);
            MDC.remove(MDC_REQUEST_METHOD);
            MDC.remove(MDC_CORRELATION_ID);
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
}
