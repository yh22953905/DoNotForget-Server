package com.hungrybrothers.alarmforsubscription.common;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class CommonResource<T> extends EntityModel<T> {
    public static <T> EntityModel<T> modelOf(T entity, Object id, Class<?> controller) {
        EntityModel<T> entityModel = EntityModel.of(entity);
        entityModel.add(linkTo(controller).slash(id).withSelfRel());
        return entityModel;
    }
}
