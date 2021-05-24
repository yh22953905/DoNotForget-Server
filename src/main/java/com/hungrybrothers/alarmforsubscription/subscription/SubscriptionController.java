package com.hungrybrothers.alarmforsubscription.subscription;

import com.hungrybrothers.alarmforsubscription.common.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Controller
@RequestMapping(Const.API_SUBSCRIPTION)
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity readSubscription(@PathVariable Long id) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return ResponseEntity.ok(subscription);
    }
}
