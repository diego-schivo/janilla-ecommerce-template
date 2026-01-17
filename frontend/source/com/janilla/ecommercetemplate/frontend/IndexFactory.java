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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.janilla.cms.CmsFrontend;
import com.janilla.ecommercetemplate.frontend.Index.Template;
import com.janilla.frontend.Frontend;
import com.janilla.frontend.resources.FrontendResources;
import com.janilla.web.DefaultFile;
import com.janilla.web.FileMap;

public class IndexFactory {

	protected final Properties configuration;

	protected final DataFetching dataFetching;

	protected final FileMap fileMap;

	public IndexFactory(Properties configuration, DataFetching dataFetching, FileMap fileMap) {
		this.configuration = configuration;
		this.dataFetching = dataFetching;
		this.fileMap = fileMap;
	}

	public Index index(FrontendExchange exchange) {
		return new Index(imports(), configuration.getProperty("ecommerce-template.api.url"), state(exchange),
				templates(), null, configuration.getProperty("ecommerce-template.stripe.publishable-key"));
	}

	protected Map<String, Object> state(FrontendExchange exchange) {
		var x = new LinkedHashMap<String, Object>();
		x.put("user", exchange.sessionUser());
		x.put("header", dataFetching.header());
		x.put("footer", dataFetching.footer());
//		x.put("enums", dataFetching.enums());
		return x;
	}

	protected Map<String, String> imports() {
		class A {
			private static Map<String, String> x;
		}
		if (A.x == null)
			synchronized (A.class) {
				if (A.x == null) {
					A.x = new LinkedHashMap<String, String>();
					Frontend.putImports(A.x);
					FrontendResources.putImports(A.x);
					CmsFrontend.putImports(A.x);
					Stream.of("admin", "admin-dashboard", "admin-fields")
							.forEach(x -> A.x.put(x, "/custom-" + x + ".js"));
					Stream.of("account", "account-nav", "address-edit", "address-item", "addresses",
							"admin-variant-options", "app", "call-to-action", "card", "cart-modal", "checkout",
							"checkout-addresses", "confirm-order", "content", "create-account", "create-address-modal",
							"find-order", "footer", "header", "hero", "intl-format", "link", "loading-spinner", "login",
							"logout", "media-block", "message", "mobile-menu", "not-found", "order", "order-item",
							"orders", "page", "payment", "price", "product", "product-description", "product-gallery",
							"product-item", "select", "shop", "variant-selector")
							.forEach(x -> A.x.put(x, "/" + x + ".js"));
				}
			}
		return A.x;
	}

	protected List<Template> templates() {
		return Stream.of("app", "cart-modal", "footer", "header", "janilla-logo", "link", "mobile-menu", "toaster")
				.map(this::template).collect(Collectors.toCollection(ArrayList::new));
	}

	public Template template(String name) {
		class A {
			private static Map<String, Template> x = new HashMap<>();
		}
		if (!A.x.containsKey(name))
			synchronized (A.class) {
				if (!A.x.containsKey(name)) {
					var f = (DefaultFile) fileMap.get("/" + name + ".html");
					try (var in = f != null ? f.newInputStream() : null) {
						A.x.put(name, in != null ? new Template(name, new String(in.readAllBytes())) : null);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			}
		return A.x.get(name);
	}
}
