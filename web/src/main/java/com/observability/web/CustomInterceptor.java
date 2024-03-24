package com.observability.web;

import io.opentelemetry.api.trace.Span;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CustomInterceptor implements HandlerInterceptor {
    private final Log log = LogFactory.getLog(this.getClass());

    private final String TRACE_ID = "traceId";
    private final String SPAN_ID = "spanId";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = Span.current().getSpanContext().getTraceId();
        String spanId = Span.current().getSpanContext().getSpanId();

        log.info("traceId: " + traceId);
        log.info("spanId: " + spanId);
        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, spanId);

        log.info("in interceptor preHandle");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
