package com.example.shieldus.config.judge0;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "compile")
@Getter
@Setter
public class JudgeProperties {
    private String judge0Url;
}
