package com.example.shieldus.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.Duration;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger defaultLog = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final Logger requestLog = LoggerFactory.getLogger("request.logger");
    private static final Logger responseLog = LoggerFactory.getLogger("response.logger");
    private static final Logger errorLog = LoggerFactory.getLogger("error.logger");


    private final MeterRegistry meterRegistry;

    private final Counter totalRequestsCounter;
    private final Counter userRequestsCounter;
    private final Counter methodRequestsCounter;
    private final Counter uriRequestsCounter;
    private final Timer requestLatencyTimer;

    // 생성자에서 DailyRequestStatisticsService 파라미터를 제거합니다.
    public LoggingInterceptor(MeterRegistry meterRegistry) {

        this.meterRegistry = meterRegistry;
        this.totalRequestsCounter = meterRegistry.counter("http.requests.total");
        this.userRequestsCounter = meterRegistry.counter("http.requests.by.user", "user", "anonymous");
        this.methodRequestsCounter = meterRegistry.counter("http.requests.by.method", "method", "GET");
        this.uriRequestsCounter = meterRegistry.counter("http.requests.by.uri", "uri", "/");

        this.requestLatencyTimer = meterRegistry.timer("http.request.latency");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        // DailyRequestStatisticsService 호출 로직을 제거합니다.
        // requestStatisticsService.incrementRequestCount(LocalDate.now());

        String username = getUsername();
        String httpMethod = request.getMethod();
        String requestUri = request.getRequestURI();

        totalRequestsCounter.increment();
        meterRegistry.counter("http.requests.by.user", "user", username).increment();
        meterRegistry.counter("http.requests.by.method", "method", httpMethod).increment();
        meterRegistry.counter("http.requests.by.uri", "uri", requestUri).increment();

        requestLog.info("[REQUEST] {} {} (User: {})", httpMethod, requestUri, username);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long durationMillis = System.currentTimeMillis() - startTime;
        String username = getUsername();
        String httpMethod = request.getMethod();
        String requestUri = request.getRequestURI();

        requestLatencyTimer.record(Duration.ofMillis(durationMillis));

        if (ex != null) {
            errorLog.error("[ERROR] {} {} ({} ms, User: {})", httpMethod, requestUri, durationMillis, username, ex);
            meterRegistry.counter("http.requests.errors.total", "uri", requestUri, "method", httpMethod).increment();
        } else {
            responseLog.info("[RESPONSE] {} {} ({} ms, User: {})", httpMethod, requestUri, durationMillis, username);
        }

        defaultLog.debug("Interceptor afterCompletion for {}", request.getRequestURI());
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return "Anonymous";
    }
}