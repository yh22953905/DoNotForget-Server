package com.hungrybrothers.alarmforsubscription.subscription;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import com.hungrybrothers.alarmforsubscription.common.CommonTest;
import com.hungrybrothers.alarmforsubscription.common.Const;

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

    @Test
    public void updateSubscription() throws Exception {
        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
            .url("http://www.naver.com")
            .cycle(1000L * 60 * 60 * 24 * 60)
            // .nextReminderDateTime(LocalDateTime.now())
            .build();

        mockMvc.perform(
                patch(Const.API_SUBSCRIPTION + "/{id}", testSubscriptionId)
                    .header(Const.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(subscriptionRequest))
            )
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("update-subscription"));

        Subscription subscription = subscriptionRepository.findById(testSubscriptionId)
            .orElseThrow(EntityNotFoundException::new);

        assertThat(subscription.getUrl()).isEqualTo(subscriptionRequest.getUrl());
        assertThat(subscription.getCycle()).isEqualTo(subscriptionRequest.getCycle());
    }
}
