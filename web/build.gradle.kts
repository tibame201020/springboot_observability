plugins {
	java
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.observability"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// grafana loki
	implementation("com.github.loki4j:loki-logback-appender:1.5.0")
	// expose prometheus
	implementation ("io.micrometer:micrometer-registry-prometheus")
	// mirco service trace
	implementation ("io.micrometer:micrometer-tracing-bridge-otel")
	// grafana tempo otlp port
	implementation ("io.opentelemetry:opentelemetry-exporter-otlp")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
