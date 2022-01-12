package com.hungrybrothers.alarmforsubscription.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.slack.api.Slack;
import org.springframework.beans.factory.annotation.Value;

import static com.slack.api.webhook.WebhookPayloads.payload;

public class SlackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    @Value("${slack.webhook.url}")
    private String webHookUrl;

    @Override
    protected void append(ILoggingEvent event) {
        try (Slack slack = Slack.getInstance()) {
            slack.send(webHookUrl, payload(p -> p.text(event.toString())));
        } catch (Exception e) {
            e.printStackTrace();
            addError("Error posting log to Slack.com (" + webHookUrl + "): " + event, e);
        }
    }
}
