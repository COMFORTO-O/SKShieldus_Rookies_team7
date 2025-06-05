package com.example.shieldus.config.judge0;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "judge0")
@Getter
@Setter
public class JudgeProperties {
    private String url;
}
