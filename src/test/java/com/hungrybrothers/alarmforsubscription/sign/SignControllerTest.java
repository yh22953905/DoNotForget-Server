package com.hungrybrothers.alarmforsubscription.sign;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.ResultActions;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountRole;
import com.hungrybrothers.alarmforsubscription.common.CommonTest;
import com.hungrybrothers.alarmforsubscription.common.Const;
import com.hungrybrothers.alarmforsubscription.exception.ErrorCode;
import com.hungrybrothers.alarmforsubscription.exception.VerifyCodeException;
import com.hungrybrothers.alarmforsubscription.security.JwtProperties;

public class SignControllerTest extends CommonTest {
    private static final String TEST_USER_ID2 = "user_id2@email.com";
    private static final String TEST_USERNAME2 = "username2";
    private static final String TEST_PASSWORD2 = "password2!";

    private static final String INVALID_USER_ID = "invalid-user-id!email.com";
    private static final String INVALID_USERNAME = "invalid-username";
    private static final String INVALID_PASSWORD = "invalid-password";

    @Test
    @DisplayName("회원 가입 - Created")
    void signUpCreated() throws Exception {
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .userId(TEST_USER_ID2)
            .username(TEST_USERNAME2)
            .password(TEST_PASSWORD2)
            .accountRole(AccountRole.CLIENT.name())
            .build();

        getSignUpActions(signUpRequest)
            .andExpect(status().isOk())
            .andDo(document("sign-up"))
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("userId").exists())
            .andExpect(jsonPath("username").exists())
            .andExpect(jsonPath("password").doesNotExist())
            .andExpect(jsonPath("roles").doesNotExist());
    }

    @Test
    @DisplayName("회원 가입 - 아이디, 닉네임, 비밀번호가 유효하지 않은 경우")
    void signUpInvalidInput() throws Exception {
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .userId(INVALID_USER_ID)
            .username(INVALID_USERNAME)
            .password(INVALID_PASSWORD)
            .accountRole(AccountRole.CLIENT.name())
            .build();

        getSignUpActions(signUpRequest)
            .andExpect(status().isBadRequest())
            .andDo(document("sign-up-invalid"))
            .andExpect(jsonPath("message", containsString(Const.VALID_MESSAGE_EMAIL)))
            .andExpect(jsonPath("message", containsString(Const.VALID_MESSAGE_USERNAME)))
            .andExpect(jsonPath("message", containsString(Const.VALID_MESSAGE_PASSWORD)));
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

    @Test
    @DisplayName("토큰 갱신 - OK")
    void refreshToken() throws Exception {
        savedAccount.setRefreshToken(jwtTokenProvider.createRefreshToken());
        Account updatedAccount = accountRepository.save(savedAccount);

        RefreshTokenRequest request = RefreshTokenRequest.builder()
            .refreshToken(updatedAccount.getRefreshToken())
            .build();

        getRefreshTokenActions(request)
            .andExpect(status().isOk())
            .andDo(document("refresh-token"))
            .andExpect(jsonPath("$.jwtToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty());

        Account account = accountRepository.findByRefreshToken(updatedAccount.getRefreshToken())
            .orElseThrow(() -> new UsernameNotFoundException(updatedAccount.getRefreshToken()));

        assertThat(account.getRefreshToken()).isNotEmpty();
    }

    @Test
    @DisplayName("토큰 갱신 - 만료된 refresh token")
    void refreshTokenInvalid() throws Exception {
        String invalidRefreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbIkNMSUVOVCIsIkFETUlOIl0sImV4cCI6MTY0MDY1MzYxOSwidXNlcklkIjoieWgyMjk1MzkwNUBnbWFpbC5jb20iLCJpYXQiOjE2NDMyNDU2MTl9.uSSKly2ksZVvY1N8k1aMt5DzVcWjnVsol7OpcARgdxQ";

        RefreshTokenRequest request = RefreshTokenRequest.builder()
            .refreshToken(invalidRefreshToken)
            .build();

        getRefreshTokenActions(request)
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof JWTVerificationException))
            .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getMessage(), ErrorCode.INVALID_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("토큰 갱신 - 빈 문자열")
    void refreshTokenEmpty() throws Exception {
        String invalidRefreshToken = "";

        RefreshTokenRequest request = RefreshTokenRequest.builder()
            .refreshToken(invalidRefreshToken)
            .build();

        getRefreshTokenActions(request)
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof JWTVerificationException))
            .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getMessage(), ErrorCode.INVALID_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("메일 발송 - OK")
    void sendEmailOk() throws Exception {
        // When
        mockMvc.perform(post(Const.API_SIGN + "/email")
                .header(JwtProperties.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("send-email"));

        // Then
        Account account = accountRepository.findByUserId(savedAccount.getUserId())
            .orElseThrow(() -> new UsernameNotFoundException(savedAccount.getUserId()));

        assertThat(account.getVerifyCode()).hasSize(6);
    }

    @Test
    @DisplayName("이메일 검증 - OK")
    void verifyEmailOk() throws Exception {
        // Given
        String code = mailUtils.generateCode();

        savedAccount.setVerifyCode(code);
        accountRepository.save(savedAccount);

        // When
        VerifyEmailRequest request = VerifyEmailRequest.builder().verifyCode(code).build();

        mockMvc.perform(patch(Const.API_SIGN + "/email")
                .header(JwtProperties.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("verify-email"));

        // Then
        Account account = accountRepository.findByUserId(savedAccount.getUserId())
            .orElseThrow(() -> new UsernameNotFoundException(savedAccount.getUserId()));

        assertTrue(account.isVerified());
    }

    @Test
    @DisplayName("이메일 검증 - 유효하지 않은 인증 코드")
    void verifyEmailInvalidVerifyCode() throws Exception {
        // Given
        String generatedCode = mailUtils.generateCode();
        String invalidCode = "123456";

        savedAccount.setVerifyCode(generatedCode);
        accountRepository.save(savedAccount);

        // When & Then
        VerifyEmailRequest request = VerifyEmailRequest.builder().verifyCode(invalidCode).build();

        mockMvc.perform(patch(Const.API_SIGN + "/email")
                .header(JwtProperties.REQUEST_HEADER_AUTHORIZATION, jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ErrorCode.VERIFY_CODE_EXCEPTION.getMessage()))
            .andExpect(jsonPath("$.status").value(ErrorCode.VERIFY_CODE_EXCEPTION.getStatus()))
            .andExpect(jsonPath("$.code").value(ErrorCode.VERIFY_CODE_EXCEPTION.getCode()))
            .andDo(print())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof VerifyCodeException))
            .andExpect(result -> assertEquals(Objects.requireNonNull(result.getResolvedException()).getMessage(), ErrorCode.VERIFY_CODE_EXCEPTION.getMessage()));
    }

    private ResultActions getSignUpActions(SignUpRequest signUpRequest) throws Exception {
        return mockMvc.perform(post(Const.API_SIGN + "/up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
            .andDo(print());
    }

    @NotNull
    private ResultActions getRefreshTokenActions(RefreshTokenRequest request) throws Exception {
        return mockMvc.perform(post(Const.API_SIGN + "/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print());
    }
}
