package com.hungrybrothers.alarmforsubscription.subscription;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
	private final SubscriptionRepository subscriptionRepository;

	public void updateSubscription(Long id, SubscriptionRequest subscriptionRequest) {
		Subscription subscription = subscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		subscription.setUrl(subscriptionRequest.getUrl());
		subscription.setCycle(subscriptionRequest.getCycle());
		subscription.setNextReminderDateTime(subscriptionRequest.getNextReminderDateTime());

		subscriptionRepository.save(subscription);
	}
}
