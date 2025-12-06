package com.janilla.ecommercetemplate.backend;

import java.math.BigDecimal;

import com.janilla.persistence.CrudObserver;
import com.janilla.persistence.Persistence;

public class CartCrudObserver implements CrudObserver<Cart> {

	protected final Persistence persistence;

	public CartCrudObserver(Persistence persistence) {
		this.persistence = persistence;
	}

	@Override
	public Cart beforeCreate(Cart entity) {
		return entity
				.withSubtotal(entity.items().stream()
						.map(x -> persistence.crud(Variant.class).read(x.variant()).priceInUsd()
								.multiply(new BigDecimal(x.quantity())))
						.reduce((x, y) -> x.add(y)).orElse(BigDecimal.ZERO));
	}

	@Override
	public Cart beforeUpdate(Cart entity) {
		return beforeCreate(entity);
	}
}
