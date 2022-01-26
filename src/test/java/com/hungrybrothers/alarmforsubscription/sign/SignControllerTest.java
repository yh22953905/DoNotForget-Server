package com.hungrybrothers.alarmforsubscription.sign;

import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountRole;
import com.hungrybrothers.alarmforsubscription.common.CommonTest;
import com.hungrybrothers.alarmforsubscription.common.Const;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SignControllerTest extends CommonTest {
    private static final String TEST_USER_ID2 = "user_id@email.com";
    private static final String TEST_USERNAME2 = "username";
    private static final String TEST_PASSWORD2 = "password";

    @Test
    @DisplayName("회원 가입 - Created")
    void signUpCreated() throws Exception {
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .userId(TEST_USER_ID2)
            .username(TEST_USERNAME2)
            .password(TEST_PASSWORD2)
            .accountRole(AccountRole.CLIENT.name())
            .build();

        mockMvc.perform(post(Const.API_SIGN + "/up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("sign-up"))
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("userId").exists())
            .andExpect(jsonPath("username").exists())
            .andExpect(jsonPath("password").doesNotExist())
            .andExpect(jsonPath("roles").doesNotExist());
    }

    @Test
    @DisplayName("로그인 - OK")
    void signInOK() throws Exception {
        SignInRequest signInRequest = SignInRequest.builder()
            .userId(TEST_USER_ID)
            .password(TEST_PASSWORD)
            .build();

        mockMvc.perform(post(Const.API_SIGN + "/in")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("sign-in"))
            .andExpect(jsonPath("$.jwtToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        Account account = accountRepository.findByUserId(signInRequest.getUserId())
            .orElseThrow(() -> new UsernameNotFoundException(signInRequest.getUserId()));

        assertThat(account.getRefreshToken()).isNotEmpty();
    }
}