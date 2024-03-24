package com.observability.web;

import io.opentelemetry.api.trace.Span;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestApi {
    private final Log log = LogFactory.getLog(this.getClass());
    @RequestMapping("/test")
    public String test() {
        log.info("in api controller");
        return "return by controller";
    }
}
