package com.janilla.ecommercetemplate.backend;

import java.util.List;
import java.util.UUID;

public record Cart(List<CartItem> items) {

	public Cart withNonNullItemIds() {
		return items != null && items.stream().anyMatch(x -> x.id() == null)
				? new Cart(items.stream().map(x -> x.id() == null ? x.withId(UUID.randomUUID()) : x).toList())
				: this;
	}
}
