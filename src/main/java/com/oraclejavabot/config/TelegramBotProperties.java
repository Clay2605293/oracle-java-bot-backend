package com.oraclejavabot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotProperties {

    private boolean enabled;
    private String token;
    private String name;
    private Integer defaultPriorityId = 2;
    private Integer defaultStatusId = 1;
    private Integer defaultDueDays = 7;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDefaultPriorityId() {
        return defaultPriorityId;
    }

    public void setDefaultPriorityId(Integer defaultPriorityId) {
        this.defaultPriorityId = defaultPriorityId;
    }

    public Integer getDefaultStatusId() {
        return defaultStatusId;
    }

    public void setDefaultStatusId(Integer defaultStatusId) {
        this.defaultStatusId = defaultStatusId;
    }

    public Integer getDefaultDueDays() {
        return defaultDueDays;
    }

    public void setDefaultDueDays(Integer defaultDueDays) {
        this.defaultDueDays = defaultDueDays;
    }
}
