package com.janilla.ecommercetemplate.backend;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.janilla.cms.Document;
import com.janilla.cms.DocumentStatus;
import com.janilla.cms.Types;
import com.janilla.persistence.Index;
import com.janilla.persistence.Store;

@Store
public record Cart(Long id, List<CartItem> items, @Index @Types(User.class) Long customer, Instant purchasedAt,
		CartStatus status, BigDecimal subtotal, Currency currency, Instant createdAt, Instant updatedAt,
		DocumentStatus documentStatus, Instant publishedAt) implements Document<Long> {

	public Cart withCustomer(Long customer) {
		return new Cart(id, items, customer, purchasedAt, status, subtotal, currency, createdAt, updatedAt,
				documentStatus, publishedAt);
	}

	public Cart withPurchasedAt(Instant purchasedAt) {
		return new Cart(id, items, customer, purchasedAt, status, subtotal, currency, createdAt, updatedAt,
				documentStatus, publishedAt);
	}

	public Cart withSubtotal(BigDecimal subtotal) {
		return new Cart(id, items, customer, purchasedAt, status, subtotal, currency, createdAt, updatedAt,
				documentStatus, publishedAt);
	}
}
