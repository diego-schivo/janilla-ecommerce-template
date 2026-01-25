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
package com.janilla.ecommercetemplate.frontend;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import com.janilla.blanktemplate.frontend.BlankDataFetching;
import com.janilla.blanktemplate.frontend.Index;
import com.janilla.blanktemplate.frontend.Index.Template;
import com.janilla.http.HttpExchange;
import com.janilla.web.ResourceMap;
import com.janilla.websitetemplate.frontend.WebsiteIndexFactory;

public class EcommerceIndexFactory extends WebsiteIndexFactory {

	public EcommerceIndexFactory(Properties configuration, String configurationKey, BlankDataFetching dataFetching,
			ResourceMap resourceMap) {
		super(configuration, configurationKey, dataFetching, resourceMap);
	}

	@Override
	public Index index(HttpExchange exchange) {
		return new EcommerceIndex(imports(), configuration.getProperty(configurationKey + ".api.url"), state(exchange),
				templates(), null, configuration.getProperty(configurationKey + ".stripe.publishable-key"));
	}

	@Override
	protected void putImports(Map<String, String> map) {
		super.putImports(map);
		Stream.of("admin-fields").forEach(x -> map.put(x, "/custom-" + x + ".js"));
		Stream.of("account", "account-nav", "address-edit", "address-item", "addresses", "admin-variant-options",
				"card", "cart-modal", "checkout", "checkout-addresses", "confirm-order", "create-account",
				"create-address-modal", "find-order", "intl-format", "loading-spinner", "login", "logout", "message",
				"mobile-menu", "order", "order-item", "orders", "payment", "price", "product", "product-description",
				"product-gallery", "product-item", "select", "shop", "variant-selector")
				.forEach(x -> map.put(x, "/" + x + ".js"));
	}

	@Override
	protected void addTemplates(List<Template> list) {
		super.addTemplates(list);
		Stream.of("cart-modal", "mobile-menu", "toaster").map(this::template).forEach(list::add);
	}
}
