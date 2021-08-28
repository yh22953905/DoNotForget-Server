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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        subscription.setCreateUser(savedAccount);
        subscription.setUpdateUser(savedAccount);
        subscription.setCreateDateTime(LocalDateTime.now());
        subscription.setUpdateDateTime(LocalDateTime.now());

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        testSubscriptionId = savedSubscription.getId();
    }

    @Test
    public void readSubscription() throws Exception {
        mockMvc.perform(
                get(Const.API_SUBSCRIPTION + "/{id}", testSubscriptionId)
                        .header(Const.REQUEST_HEADER_AUTHORIZATION, jwtToken)
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
                        .header(Const.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("read-subscriptions"));
    }

    @Test
    public void createSubscription() throws Exception {
        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
                .url("http://www.google.com")
                .cycle(1000L * 60 * 60 * 24 * 30)
//                .nextReminderDateTime(LocalDateTime.now())
                .build();

        mockMvc.perform(
                post(Const.API_SUBSCRIPTION)
                        .header(Const.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionRequest))
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("create-subscription"));
    }
}