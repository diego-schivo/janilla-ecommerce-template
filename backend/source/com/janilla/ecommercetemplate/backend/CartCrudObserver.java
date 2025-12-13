package com.janilla.ecommercetemplate.backend;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Random;

import com.janilla.persistence.CrudObserver;
import com.janilla.persistence.Persistence;

public class CartCrudObserver implements CrudObserver<Cart> {

	private static final Random RANDOM = new SecureRandom();

	protected final Persistence persistence;

	public CartCrudObserver(Persistence persistence) {
		this.persistence = persistence;
	}

	@Override
	public Cart beforeCreate(Cart entity) {
		var c = entity;
		if (c.customer() == null && c.secret() == null) {
			var bb = new byte[20];
			RANDOM.nextBytes(bb);
			c = c.withSecret(HexFormat.of().formatHex(bb));
		}
		return computeSubtotal(c);
	}

	@Override
	public Cart beforeUpdate(Cart entity) {
		return computeSubtotal(entity);
	}

	protected Cart computeSubtotal(Cart cart) {
		return cart.withSubtotal(cart.items().stream().map(x -> {
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
}
