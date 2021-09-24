package com.hungrybrothers.alarmforsubscription.hateoas;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import com.hungrybrothers.alarmforsubscription.common.CommonTest;
import com.hungrybrothers.alarmforsubscription.common.Const;

public class HateoasControllerTest extends CommonTest {
	@Test
	@DisplayName("HATEOAS 인덱스 조회 성공")
	public void getIndices() throws Exception {
		mockMvc.perform(
				get(Const.API_HATEOAS)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaTypes.HAL_JSON)
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("read-indices"))
			.andExpect(jsonPath("_links.sign.href").exists())
			.andExpect(jsonPath("_links.subscriptions.href").exists());
	}
}
