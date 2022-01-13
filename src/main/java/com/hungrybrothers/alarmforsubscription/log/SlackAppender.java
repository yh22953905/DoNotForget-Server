package com.hungrybrothers.alarmforsubscription.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.slack.api.Slack;

import static com.slack.api.webhook.WebhookPayloads.payload;

public class SlackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private String webHookUrl = "https://hooks.slack.com/services/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";

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
