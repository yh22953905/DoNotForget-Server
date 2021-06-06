package com.hungrybrothers.alarmforsubscription.sign;

import com.hungrybrothers.alarmforsubscription.common.CommonTest;
import com.hungrybrothers.alarmforsubscription.common.Const;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class SignControllerTest extends CommonTest {
    @Test
    public void signInWithOpenID() throws Exception {
        SignDto signDto = SignDto.builder()
                .openIdToken(testToken)
                .type("KAKAO OR NAVER")
                .build();

        mockMvc.perform(
                post(Const.API_SIGN + "/in/openid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(signDto))
        )
                .andDo(print())
                .andDo(document("social-login"));
    }
}