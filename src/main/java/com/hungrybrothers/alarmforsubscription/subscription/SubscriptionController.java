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
    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper modelMapper;

    @GetMapping(path = "/{id}")
    public ResponseEntity<EntityModel<Subscription>> readSubscription(@PathVariable Long id) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        EntityModel<Subscription> entityModel = CommonResource.modelOf(subscription, subscription.getId(), SubscriptionController.class);

        entityModel.add(Link.of("resources-subscription-read").withRel(LinkRelation.of("profile")));

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping(path = "/account")
    public ResponseEntity<PagedModel<EntityModel<Subscription>>> readSubscriptionsByAccount(
            @PageableDefault(size = 100, sort = "nextReminderDateTime") Pageable pageable
            , PagedResourcesAssembler<Subscription> assembler
            , @CurrentAccount Account account
    ) {
        Page<Subscription> page = subscriptionRepository.findAllByCreateUser(account, pageable);

        PagedModel<EntityModel<Subscription>> entityModels = assembler.toModel(page, subscription -> CommonResource.modelOf(subscription, subscription.getId(), SubscriptionController.class));

        entityModels.add(Link.of("resources-subscriptions-read").withRel(LinkRelation.of("profile")));

        return ResponseEntity.ok(entityModels);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Subscription>> createSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        Subscription subscription = modelMapper.map(subscriptionRequest, Subscription.class);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        EntityModel<Subscription> entityModel = CommonResource.modelOf(savedSubscription, savedSubscription.getId(), SubscriptionController.class);

        return ResponseEntity.ok(entityModel);
    }
}
