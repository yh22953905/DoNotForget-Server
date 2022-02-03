package com.hungrybrothers.alarmforsubscription.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailException;

import com.hungrybrothers.alarmforsubscription.common.CommonTest;

public class MailUtilsTest extends CommonTest {
	private final String VALID_EMAIL = "user_id@email.com";
	private final String INVALID_EMAIL = "invalid-email";

	@Disabled
	@Test
	@DisplayName("메일 발송 - 성공")
	public void sendMailOk() {
		mailUtils.sendMail(VALID_EMAIL, mailUtils.generateCode());
	}

	@Disabled
	@Test
	@DisplayName("메일 발송 - 유효하지 않은 이메일 주소")
	public void sendMailInvalidEmailAddress() {
		Assertions.assertThrows(MailException.class,
			() -> mailUtils.sendMail(INVALID_EMAIL, mailUtils.generateCode()));
	}

	@Test
	@DisplayName("인증 코드 생성")
	public void generateCode() {
		String code = mailUtils.generateCode();

		assertThat(code).isNotEmpty();
		assertThat(code).hasSize(6);
	}
}
