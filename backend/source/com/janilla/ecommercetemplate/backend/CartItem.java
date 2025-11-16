package com.janilla.ecommercetemplate.backend;

import java.math.BigDecimal;
import java.util.UUID;

import com.janilla.cms.Types;

public record CartItem(UUID id, @Types(Product.class) Long product, UUID variantId, BigDecimal unitPrice,
		Integer quantity) {

	public CartItem withId(UUID id) {
		return new CartItem(id, product, variantId, unitPrice, quantity);
	}
}
