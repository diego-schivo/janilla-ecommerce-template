package com.janilla.ecommercetemplate.backend;

import java.math.BigDecimal;
import java.util.Objects;

import com.janilla.persistence.CrudObserver;
import com.janilla.persistence.Persistence;

public class CartCrudObserver implements CrudObserver<Cart> {

	protected final Persistence persistence;

	public CartCrudObserver(Persistence persistence) {
		this.persistence = persistence;
	}

	@Override
	public Cart beforeCreate(Cart entity) {
		return entity.withSubtotal(entity.items().stream().map(x -> {
			BigDecimal p;
			if (x.variant() != null)
				p = persistence.crud(Variant.class).read(x.variant()).priceInUsd();
			else if (x.product() != null)
				p = persistence.crud(Product.class).read(x.product()).priceInUsd();
			else
				p = null;
			return p != null ? p.multiply(new BigDecimal(x.quantity())) : null;
		}).filter(Objects::nonNull).reduce((x, y) -> x.add(y)).orElse(BigDecimal.ZERO));
	}

	@Override
	public Cart beforeUpdate(Cart entity) {
		return beforeCreate(entity);
	}
}
