package edu.qs.attendance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;

/*
 * TICKET LF-205 (part of the fix):
 * A slow external government API must NOT be able to freeze the app or hold a DB connection.
 * 1. This client has explicit connect/read timeouts.
 * 2. Callers must invoke it BEFORE opening a @Transactional block, never inside one.
 */
@Service
public class MinimumWageApiClient {

    private static final Logger log = LoggerFactory.getLogger(MinimumWageApiClient.class);
    private final RestTemplate restTemplate;

    public MinimumWageApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }

    // Returns the latest statutory minimum wage. Falls back to a default if the API is slow/down.
    public BigDecimal fetchLatestMinimumWage() {
        try {
            // TODO: replace with the real endpoint. Stubbed for the assignment.
            // return restTemplate.getForObject("https://gov.example/minwage", WageResponse.class).rate();
            return new BigDecimal("600.00");
        } catch (Exception ex) {
            log.warn("Minimum-wage API unavailable, using fallback. Cause: {}", ex.getMessage());
            return new BigDecimal("600.00");
        }
    }
}
