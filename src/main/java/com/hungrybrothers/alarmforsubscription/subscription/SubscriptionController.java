package com.hungrybrothers.alarmforsubscription.subscription;

import com.hungrybrothers.alarmforsubscription.common.CommonResource;
import com.hungrybrothers.alarmforsubscription.common.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Controller
@RequestMapping(Const.API_SUBSCRIPTION)
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping(path = "/{id}")
    public ResponseEntity readSubscription(@PathVariable Long id) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return ResponseEntity.ok(subscription);
    }

    @GetMapping
    public ResponseEntity readSubscriptionsByUser(@RequestBody SubscriptionDto subscriptionDto
            , @PageableDefault(size = 100, page = 0, sort = "nextReminderDateTime") Pageable pageable
            , PagedResourcesAssembler<Subscription> assembler
    ) {
        Page<Subscription> page = subscriptionRepository.findAllByAccount(pageable);

        PagedModel<EntityModel<Subscription>> entityModels = assembler.toModel(page, subscription -> CommonResource.modelOf(subscription, subscription.getId(), SubscriptionController.class));

        entityModels.add(Link.of("resources-subscriptions-read").withRel(LinkRelation.of("profile")));

        return ResponseEntity.ok(page);
    }
}
