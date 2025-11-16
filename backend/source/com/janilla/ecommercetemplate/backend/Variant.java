package com.janilla.ecommercetemplate.backend;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record Variant(UUID id, Boolean active, Set<Object> options, BigDecimal price, Long stock) {

	public Variant withId(UUID id) {
		return new Variant(id, active, options, price, stock);
	}
}
