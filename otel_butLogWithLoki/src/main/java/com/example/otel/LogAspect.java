package com.example.otel;


import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogAspect {
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";

    @Before("execution(* org.slf4j.Logger.*(..))")
    private void beforeLogger() {
        SpanContext spanContext = Span.current().getSpanContext();
        MDC.put(TRACE_ID, spanContext.getTraceId());
        MDC.put(SPAN_ID, spanContext.getSpanId());
    }
}
