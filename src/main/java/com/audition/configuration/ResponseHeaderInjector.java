package com.audition.configuration;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to inject OpenTelemetry trace and span IDs into HTTP response headers.
 */
@Component
@NoArgsConstructor
public class ResponseHeaderInjector implements HandlerInterceptor {

    private Tracer tracer;

    /**
     * Initializes the interceptor with a tracer instance.
     *
     * @param tracer the OpenTelemetry tracer
     */
    public ResponseHeaderInjector(final Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * Adds trace and span IDs to the HTTP response headers before request processing.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param handler  the request handler
     * @return true to continue request processing
     * @throws Exception if an error occurs
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
        throws Exception {
        final Span span = tracer.spanBuilder("custom-span").startSpan();
        try (Scope ignored = span.makeCurrent()) {
            response.addHeader("trace-id", span.getSpanContext().getTraceId());
            response.addHeader("span-id", span.getSpanContext().getSpanId());
        } finally {
            span.end();
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
