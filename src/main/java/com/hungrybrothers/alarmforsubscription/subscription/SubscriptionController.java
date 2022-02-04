package com.hungrybrothers.alarmforsubscription.subscription;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.CurrentAccount;
import com.hungrybrothers.alarmforsubscription.common.Const;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(Const.API_SUBSCRIPTION)
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<EntityModel<SubscriptionResponse>> readSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.readSubscription(id));
    }

    @GetMapping(path = "/account")
    public ResponseEntity<PagedModel<EntityModel<SubscriptionResponse>>> readSubscriptionsByAccount(
        @PageableDefault(size = 100, sort = "nextReminderDateTime") Pageable pageable,
        PagedResourcesAssembler<Subscription> assembler, @CurrentAccount Account account) {
        return ResponseEntity.ok(subscriptionService.readSubscriptionsByAccount(pageable, assembler, account));
    }

    @PostMapping
    public ResponseEntity<EntityModel<SubscriptionResponse>> createSubscription(@RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.createSubscription(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateSubscription(@PathVariable Long id, @RequestBody SubscriptionRequest request) {
        subscriptionService.updateSubscription(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok().build();
    }
}
