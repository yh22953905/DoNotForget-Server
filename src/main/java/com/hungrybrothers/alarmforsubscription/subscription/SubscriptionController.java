package com.hungrybrothers.alarmforsubscription.subscription;

import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.common.CommonResource;
import com.hungrybrothers.alarmforsubscription.common.Const;
import com.hungrybrothers.alarmforsubscription.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Controller
@RequestMapping(Const.API_SUBSCRIPTION)
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper modelMapper;

    @GetMapping(path = "/{id}")
    public ResponseEntity<EntityModel<SubscriptionResponse>> readSubscription(@PathVariable Long id) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        EntityModel<SubscriptionResponse> entityModel = CommonResource
            .modelOf(modelMapper.map(subscription, SubscriptionResponse.class), subscription.getId(), SubscriptionController.class);

        entityModel.add(Link.of("resources-subscription-read").withRel(LinkRelation.of("profile")));

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping(path = "/account")
    public ResponseEntity<PagedModel<EntityModel<SubscriptionResponse>>> readSubscriptionsByAccount(
            @PageableDefault(size = 100, sort = "nextReminderDateTime") Pageable pageable
            , PagedResourcesAssembler<Subscription> assembler
            , @CurrentAccount Account account
    ) {
        Page<Subscription> page = subscriptionRepository.findAllByCreateUser(account, pageable);

        PagedModel<EntityModel<SubscriptionResponse>> entityModels = assembler
            .toModel(page, subscription -> CommonResource.modelOf(modelMapper.map(subscription, SubscriptionResponse.class), subscription.getId(), SubscriptionController.class));

        entityModels.add(Link.of("resources-subscriptions-read").withRel(LinkRelation.of("profile")));

        return ResponseEntity.ok(entityModels);
    }

    @PostMapping
    public ResponseEntity<EntityModel<SubscriptionResponse>> createSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        Subscription subscription = modelMapper.map(subscriptionRequest, Subscription.class);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        EntityModel<SubscriptionResponse> entityModel = CommonResource
            .modelOf(modelMapper.map(savedSubscription, SubscriptionResponse.class), savedSubscription.getId(), SubscriptionController.class);

        return ResponseEntity.ok(entityModel);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateSubscription(@PathVariable Long id, @RequestBody SubscriptionRequest subscriptionRequest) {
        subscriptionService.updateSubscription(id, subscriptionRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);

        return ResponseEntity.ok().build();
    }
}
