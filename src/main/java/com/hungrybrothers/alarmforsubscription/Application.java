package com.hungrybrothers.alarmforsubscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.ZoneId;
import java.util.TimeZone;

@EnableTransactionManagement
@EnableJpaAuditing
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")));
		SpringApplication.run(Application.class, args);
	}
}
