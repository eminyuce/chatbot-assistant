package com.yuce.chat.assistant.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom business metrics (Prometheus-compatible via Micrometer).
 * JVM, HTTP, and DB metrics are auto-configured by Spring Boot Actuator.
 */
@Configuration
public class ObservabilityConfig {

    public static final String METRIC_CHAT_REQUESTS = "chatbot.chat.requests";
    public static final String METRIC_CHAT_LATENCY = "chatbot.chat.latency";
    public static final String METRIC_INTENT_MATCHES = "chatbot.intent.matches";
    public static final String METRIC_INTENT_LATENCY = "chatbot.intent.latency";
    public static final String METRIC_ERRORS = "chatbot.errors";

    @Bean
    public ChatMetrics chatMetrics(MeterRegistry registry) {
        return new ChatMetrics(registry);
    }

    @Bean
    public IntentMetrics intentMetrics(MeterRegistry registry) {
        return new IntentMetrics(registry);
    }

    /**
     * Chat-specific counters and timers for business observability.
     */
    public record ChatMetrics(MeterRegistry registry) {

        public void recordChatRequest(String intent, String outcome) {
            registry.counter(METRIC_CHAT_REQUESTS,
                    "intent", intent != null ? intent : "unknown",
                    "outcome", outcome != null ? outcome : "success").increment();
        }

        public Timer.Sample startChatLatency() {
            return Timer.start(registry);
        }

        public void recordChatLatency(Timer.Sample sample, String intent) {
            sample.stop(Timer.builder(METRIC_CHAT_LATENCY)
                    .tag("intent", intent != null ? intent : "unknown")
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .register(registry));
        }

        public void recordError(String component, String type) {
            registry.counter(METRIC_ERRORS,
                    "component", component != null ? component : "unknown",
                    "type", type != null ? type : "unknown").increment();
        }
    }

    /**
     * Intent matching metrics (vector search, LLM extraction).
     */
    public record IntentMetrics(MeterRegistry registry) {

        public void recordIntentMatch(String intent, boolean matched) {
            registry.counter(METRIC_INTENT_MATCHES,
                    "intent", intent != null ? intent : "unknown",
                    "matched", String.valueOf(matched)).increment();
        }

        public Timer.Sample startIntentLatency() {
            return Timer.start(registry);
        }

        public void recordIntentLatency(Timer.Sample sample, String intent) {
            sample.stop(Timer.builder(METRIC_INTENT_LATENCY)
                    .tag("intent", intent != null ? intent : "unknown")
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .register(registry));
        }
    }
}
