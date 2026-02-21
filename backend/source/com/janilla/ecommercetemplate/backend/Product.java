/*
 * MIT License
 *
 * Copyright (c) 2018-2025 Payload CMS, Inc. <info@payloadcms.com>
 * Copyright (c) 2024-2026 Diego Schivo <diego.schivo@janilla.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.janilla.ecommercetemplate.backend;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.janilla.backend.cms.Types;
import com.janilla.backend.cms.Versions;
import com.janilla.cms.Document;
import com.janilla.cms.DocumentStatus;
import com.janilla.persistence.Index;
import com.janilla.persistence.Store;
import com.janilla.websitetemplate.backend.CallToAction;
import com.janilla.websitetemplate.backend.Category;
import com.janilla.websitetemplate.backend.Content;
import com.janilla.websitetemplate.backend.MediaBlock;
import com.janilla.websitetemplate.backend.Meta;

@Store
@Versions(drafts = true)
public record Product(Long id, String title, String description, List<GalleryItem> gallery, List<@Types( {
		CallToAction.class, Content.class, MediaBlock.class }) ?> layout, Boolean enableVariants,
		List<@Types({ VariantType.class }) Long> variantTypes, List<@Types(Variant.class) Long> variants,
		Boolean priceInUsdEnabled, BigDecimal priceInUsd, @Index List<@Types(Category.class) Long> categories,
		Meta meta, @Index String slug, Instant createdAt, Instant updatedAt, DocumentStatus documentStatus,
		Instant publishedAt) implements Document<Long>{

	public Product withVariants(List<Long> variants) {
		return new Product(id, title, description, gallery, layout, enableVariants, variantTypes, variants,
				priceInUsdEnabled, priceInUsd, categories, meta, slug, createdAt, updatedAt, documentStatus,
				publishedAt);
	}
}
