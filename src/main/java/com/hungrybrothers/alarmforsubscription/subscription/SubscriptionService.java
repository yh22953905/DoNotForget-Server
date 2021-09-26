package com.hungrybrothers.alarmforsubscription.subscription;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.common.CommonResource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
	private final SubscriptionRepository subscriptionRepository;
	private final ModelMapper modelMapper;

	public EntityModel<SubscriptionResponse> readSubscription(Long id) {
		Subscription subscription = subscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		EntityModel<SubscriptionResponse> entityModel = CommonResource
			.modelOf(modelMapper.map(subscription, SubscriptionResponse.class), subscription.getId(),
				SubscriptionController.class);

		entityModel.add(Link.of("resources-subscription-read").withRel(LinkRelation.of("profile")));

		return entityModel;
	}

	public PagedModel<EntityModel<SubscriptionResponse>> readSubscriptionsByAccount(Pageable pageable,
		PagedResourcesAssembler<Subscription> assembler, Account account) {
		Page<Subscription> page = subscriptionRepository.findAllByCreateUser(account, pageable);

		PagedModel<EntityModel<SubscriptionResponse>> entityModels = assembler
			.toModel(page,
				subscription -> CommonResource.modelOf(modelMapper.map(subscription, SubscriptionResponse.class),
					subscription.getId(), SubscriptionController.class));

		entityModels.add(Link.of("resources-subscriptions-read").withRel(LinkRelation.of("profile")));

		return entityModels;
	}

	public EntityModel<SubscriptionResponse> createSubscription(SubscriptionRequest request) {
		Subscription subscription = modelMapper.map(request, Subscription.class);

		Subscription savedSubscription = subscriptionRepository.save(subscription);

		return CommonResource
			.modelOf(modelMapper.map(savedSubscription, SubscriptionResponse.class), savedSubscription.getId(),
				SubscriptionController.class);
	}

	public void updateSubscription(Long id, SubscriptionRequest request) {
		Subscription subscription = subscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		subscription.setUrl(request.getUrl());
		subscription.setCycle(request.getCycle());
		subscription.setNextReminderDateTime(request.getNextReminderDateTime());

		subscriptionRepository.save(subscription);
	}

	public void deleteSubscription(Long id) {
		Subscription subscription = subscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		subscriptionRepository.delete(subscription);
	}
}
