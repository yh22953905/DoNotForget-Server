package com.hungrybrothers.alarmforsubscription.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import com.hungrybrothers.alarmforsubscription.subscription.Subscription;
import com.hungrybrothers.alarmforsubscription.subscription.SubscriptionResponse;

@Component
public class CustomModelMapper extends ModelMapper {
	@PostConstruct
	public void postConstruct() {
		Configuration configuration = super.getConfiguration();

		configuration.setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
			.setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

		Converter<Subscription, SubscriptionResponse> subscriptionConverter = CustomModelMapper::subscriptionConverter;

		addConverter(subscriptionConverter, Subscription.class, SubscriptionResponse.class);
	}

	private static SubscriptionResponse subscriptionConverter(MappingContext<Subscription, SubscriptionResponse> context) {
		Subscription source = context.getSource();
		LocalDateTime time = source.getNextReminderDateTime();

		return SubscriptionResponse.builder()
			.id(source.getId())
			.url(source.getUrl())
			.cycle(source.getCycle())
			.nextReminderDateTime(Objects.isNull(time) ? Const.HYPHEN : time.format(DateTimeFormatter.ofPattern(Const.LOCAL_DATE_TIME_FORMAT)))
			.build();
	}
}
