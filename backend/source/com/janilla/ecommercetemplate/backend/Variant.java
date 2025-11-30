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
public record Variant(Long id, String title, @Index @Types(Product.class) Long product,
		List<@Types(VariantOption.class) Long> options, Long inventory, Boolean priceInUsdEnabled,
		BigDecimal priceInUsd, Instant createdAt, Instant updatedAt, DocumentStatus documentStatus, Instant publishedAt)
		implements Document<Long> {
}
