package com.example.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
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

    /**
     * 標註resource value
     * 便於後續grafana filter
     * service: 專案名稱
     * environment: 執行環境
     */
    private Resource createApplicationResource() {
        AttributesBuilder attributesBuilder = Attributes.builder()
                .put(ResourceAttributes.SERVICE_NAME, environment.getRequiredProperty("management.metrics.tags.service"))
                .put(ResourceAttributes.DEPLOYMENT_ENVIRONMENT, environment.getRequiredProperty("management.metrics.tags.env"));
        return Resource.getDefault().merge(Resource.create(attributesBuilder.build()));
    }

    /**
     * tracer provider
     */
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

    /**
     * metric provider
     */
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
    @Bean
    public OpenTelemetry openTelemetry() {
        return OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider())
                .setMeterProvider(sdkMeterProvider())
                .buildAndRegisterGlobal();
    }
}
