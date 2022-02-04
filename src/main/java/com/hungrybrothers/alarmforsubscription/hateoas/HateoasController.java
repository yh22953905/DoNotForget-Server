package com.hungrybrothers.alarmforsubscription.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hungrybrothers.alarmforsubscription.common.Const;
import com.hungrybrothers.alarmforsubscription.sign.SignController;
import com.hungrybrothers.alarmforsubscription.subscription.SubscriptionController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(Const.API_HATEOAS)
@RequiredArgsConstructor
public class HateoasController {
	@GetMapping
	public ResponseEntity<RepresentationModel<?>> getIndices() {
		return ResponseEntity.ok(getRepresentationModel());
	}

	private RepresentationModel<?> getRepresentationModel() {
		RepresentationModel<?> representationModel = new RepresentationModel<>();

		representationModel.add(linkTo(SignController.class).withRel(Const.HATEOAS_SIGN));
		representationModel.add(linkTo(SubscriptionController.class).withRel(Const.HATEOAS_SUBSCRIPTIONS));

		return representationModel;
	}
}
