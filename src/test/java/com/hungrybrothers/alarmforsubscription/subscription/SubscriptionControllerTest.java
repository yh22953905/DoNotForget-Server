package com.hungrybrothers.alarmforsubscription.subscription;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import com.hungrybrothers.alarmforsubscription.common.CommonTest;
import com.hungrybrothers.alarmforsubscription.common.Const;
import com.hungrybrothers.alarmforsubscription.security.JwtProperties;

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
    @DisplayName("하나의 구독 정보 조회 성공")
    public void readSubscription() throws Exception {
        mockMvc.perform(
                get(Const.API_SUBSCRIPTION + "/{id}", testSubscriptionId)
                    .header(JwtProperties.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
            )
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("read-subscription"))
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("url").exists())
            .andExpect(jsonPath("cycle").exists())
            .andExpect(jsonPath("nextReminderDateTime").exists())
            .andExpect(jsonPath("_links").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andExpect(jsonPath("createDateTime").doesNotExist())
            .andExpect(jsonPath("createUser").doesNotExist())
            .andExpect(jsonPath("updateDateTime").doesNotExist())
            .andExpect(jsonPath("updateUser").doesNotExist());
    }

    @Test
    @DisplayName("구독 정보 리스트 조회 성공")
    public void readSubscriptionsByUser() throws Exception {
        mockMvc.perform(
                get(Const.API_SUBSCRIPTION + "/account")
                    .header(JwtProperties.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
            )
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("read-subscriptions"))
            .andExpect(jsonPath("_embedded.subscriptionResponseList[0].id").exists())
            .andExpect(jsonPath("_embedded.subscriptionResponseList[0].url").exists())
            .andExpect(jsonPath("_embedded.subscriptionResponseList[0].cycle").exists())
            .andExpect(jsonPath("_embedded.subscriptionResponseList[0].nextReminderDateTime").exists())
            .andExpect(jsonPath("_embedded.subscriptionResponseList[0]._links").exists())
            .andExpect(jsonPath("_links").exists())
            .andExpect(jsonPath("_links.profile").exists());
    }

    @Test
    @DisplayName("하나의 구독 정보 생성 성공")
    public void createSubscription() throws Exception {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(Const.LOCAL_DATE_TIME_FORMAT));
        String formattedNow = String.format("\"%s\"", now);

        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
            .url("http://www.google.com")
            .cycle(1000L * 60 * 60 * 24 * 30)
            .nextReminderDateTime(null)
            .build();

        mockMvc.perform(
                post(Const.API_SUBSCRIPTION)
                    .header(JwtProperties.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(subscriptionRequest).replace("null", formattedNow))
            )
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("create-subscription"))
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("url").exists())
            .andExpect(jsonPath("cycle").exists())
            .andExpect(jsonPath("nextReminderDateTime").exists())
            .andExpect(jsonPath("_links").exists())
            .andExpect(jsonPath("createDateTime").doesNotExist())
            .andExpect(jsonPath("createUser").doesNotExist())
            .andExpect(jsonPath("updateDateTime").doesNotExist())
            .andExpect(jsonPath("updateUser").doesNotExist());
    }

    @Test
    @DisplayName("하나의 구독 정보 수정 성공")
    public void updateSubscription() throws Exception {
        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
            .url("http://www.naver.com")
            .cycle(1000L * 60 * 60 * 24 * 60)
            // .nextReminderDateTime(LocalDateTime.now())
            .build();

        mockMvc.perform(
                patch(Const.API_SUBSCRIPTION + "/{id}", testSubscriptionId)
                    .header(JwtProperties.REQUEST_HEADER_AUTHORIZATION, jwtToken)
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

    @Test
    @DisplayName("하나의 구독 정보 삭제 성공")
    public void deleteSubscription() throws Exception {
        mockMvc.perform(
                delete(Const.API_SUBSCRIPTION + "/{id}", testSubscriptionId)
                    .header(JwtProperties.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
            )
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("delete-subscription"));

        assertThat(subscriptionRepository.existsById(testSubscriptionId)).isFalse();
    }
}
