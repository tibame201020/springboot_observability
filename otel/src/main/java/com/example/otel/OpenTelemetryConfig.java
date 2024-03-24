package com.example.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class OpenTelemetryConfig {
    private final Environment environment;
    private Resource createApplicationResource() {
        AttributesBuilder attributesBuilder = Attributes.builder()
                .put("application.name", "aaaaaaa")
                .put(ResourceAttributes.SERVICE_NAME, "bbbbbbbbbb")
                .put("environment", "cccccccccccc");
        return Resource.getDefault().merge(Resource.create(attributesBuilder.build()));
    }

    private SdkTracerProvider sdkTracerProvider() {
        return SdkTracerProvider.builder()
                .addSpanProcessor(
                        BatchSpanProcessor.builder(
                                OtlpHttpSpanExporter.builder()
                                        .setEndpoint(environment.getRequiredProperty("otel.tracer-provider.end-point"))
                                        .build()
                        ).build()
                )
                .setResource(createApplicationResource())
                .build();
    }

    private SdkMeterProvider sdkMeterProvider() {
        return SdkMeterProvider.builder()
                .registerMetricReader(
                        PeriodicMetricReader.builder(
                                OtlpHttpMetricExporter.builder()
                                        .setEndpoint(environment.getRequiredProperty("otel.meter-provider.end-point"))
                                        .build()
                        ).build()
                )
                .setResource(createApplicationResource())
                .build();
    }

    private SdkLoggerProvider sdkLoggerProvider() {
        return SdkLoggerProvider.builder()
                .addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(
                                OtlpGrpcLogRecordExporter.builder()
                                        .setEndpoint(environment.getRequiredProperty("otel.logger-provider.end-point"))
                                        .build()
                        ).build()
                )
                .setResource(createApplicationResource())
                .build();
    }

    @Bean
    public OpenTelemetry openTelemetry() {
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider())
                .setMeterProvider(sdkMeterProvider())
                .setLoggerProvider(sdkLoggerProvider())
                .buildAndRegisterGlobal();
        OpenTelemetryAppender.install(openTelemetry);

        return openTelemetry;
    }
}
