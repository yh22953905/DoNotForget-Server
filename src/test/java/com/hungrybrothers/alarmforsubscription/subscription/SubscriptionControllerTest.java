package com.hungrybrothers.alarmforsubscription.subscription;

import com.hungrybrothers.alarmforsubscription.common.CommonTest;
import com.hungrybrothers.alarmforsubscription.common.Const;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SubscriptionControllerTest extends CommonTest {
    private Long testSubscriptionId;

    @BeforeEach
    public void generateTestData() {
        Subscription subscription = Subscription.builder()
                .url("http://www.google.com")
                .cycle(1000L * 60 * 60 * 24 * 30)
                .nextReminderDateTime(LocalDateTime.now())
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        testSubscriptionId = savedSubscription.getId();
    }

    @Test
    public void readSubscription() throws Exception {
        mockMvc.perform(
                get(Const.API_SUBSCRIPTION + "/{id}", testSubscriptionId)
                        .header(Const.X_AUTH_TOKEN, testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("read-subscription"));
    }

    @Test
    public void readSubscriptionsByUser() throws Exception {
        mockMvc.perform(
                get(Const.API_SUBSCRIPTION + "/account")
                        .header(Const.X_AUTH_TOKEN, testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
        )
                .andExpect(status().isOk())
                .andDo(print());
    }
}