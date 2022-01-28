package com.hungrybrothers.alarmforsubscription.utils;

import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hungrybrothers.alarmforsubscription.common.Const;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailUtils {
	private final JavaMailSender javaMailSender;

	public void sendMail(String email, String code) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(email);
		message.setSubject(Const.MAIL_SUBJECT);
		message.setText(String.format(Const.MAIL_TEXT, code));

		javaMailSender.send(message);
	}

	public String generateCode() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		Random random = new Random();

		return random.ints(leftLimit, rightLimit + 1)
			.limit(Const.MAIL_CODE_LENGTH)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}
}
