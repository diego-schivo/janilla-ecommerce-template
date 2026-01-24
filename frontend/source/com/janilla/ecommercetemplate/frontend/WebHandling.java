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
import java.util.stream.Stream;

import com.janilla.web.Bind;
import com.janilla.web.Handle;
import com.janilla.web.NotFoundException;

public class WebHandling {

	protected final DataFetching dataFetching;

	protected final IndexFactory indexFactory;

	public WebHandling(DataFetching dataFetching, IndexFactory indexFactory) {
		this.dataFetching = dataFetching;
		this.indexFactory = indexFactory;
	}

	@Handle(method = "GET", path = "/account")
	public Object account(FrontendExchange exchange) {
//		IO.println("WebHandling.account");
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/account/addresses")
	public Object addresses(FrontendExchange exchange) {
//		IO.println("WebHandling.addresses");
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/admin(/[\\w\\d/-]*)?")
	public Object admin(String path, FrontendExchange exchange) {
//		IO.println("WebHandling.admin, path=" + path);
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/checkout")
	public Object checkout(FrontendExchange exchange) {
//		IO.println("WebHandling.checkout");
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/checkout/confirm-order")
	public Object confirmOrder(FrontendExchange exchange) {
//		IO.println("WebHandling.confirmOrder");
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/create-account")
	public Object createAccount(FrontendExchange exchange) {
//		IO.println("WebHandling.createAccount");
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/find-order")
	public Object findOrder(FrontendExchange exchange) {
//		IO.println("WebHandling.findOrder");
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/login")
	public Object login(FrontendExchange exchange) {
//		IO.println("WebHandling.login");
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/logout")
	public Object logout(FrontendExchange exchange) {
//		IO.println("WebHandling.logout");
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/orders/(\\d+)")
	public Object order(Long id, String guestEmail, FrontendExchange exchange) {
//		IO.println("WebHandling.order, id=" + id + ", email=" + email);
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/orders")
	public Object orders(FrontendExchange exchange) {
//		IO.println("WebHandling.orders");
		return indexFactory.index(exchange);
	}

	@Handle(method = "GET", path = "/([\\w\\d-]*)")
	public Object page(String slug, FrontendExchange exchange) {
//		IO.println("WebHandling.page, slug=" + slug);
		if (slug == null || slug.isEmpty())
			slug = "home";
		var pp = dataFetching.pages(slug, exchange.tokenCookie());
		if (pp.isEmpty()) {
			if (slug.equals("home"))
				pp = List.of(Map.of("slug", "home"));
			else
				throw new NotFoundException("slug=" + slug);
		}
		var i = indexFactory.index(exchange);
		i.state().put("page", pp.getFirst());
		Stream.of("call-to-action", "content", "hero", "media-block", "page").map(indexFactory::template)
				.forEach(i.templates()::add);
		return i;
	}

	@Handle(method = "GET", path = "/products/([\\w\\d-]+)")
	public Object product(String slug, FrontendExchange exchange) {
//		IO.println("WebHandling.product, slug=" + slug);
		var pp = dataFetching.products(slug, null, null, null, exchange.tokenCookie());
		if (pp.isEmpty())
			throw new NotFoundException("slug=" + slug);
		var i = indexFactory.index(exchange);
		i.state().put("product", pp.getFirst());
		Stream.of("call-to-action", "content", "media-block", "price", "product", "product-description",
				"product-gallery", "variant-selector").map(indexFactory::template).forEach(i.templates()::add);
		return i;
	}

	@Handle(method = "GET", path = "/shop")
	public Object shop(@Bind("q") String query, Long category, String sort, FrontendExchange exchange) {
//		IO.println("WebHandling.shop, query=" + query + ", category=" + category);
		var i = indexFactory.index(exchange);
		i.state().put("categories", dataFetching.categories());
		i.state().put("products", dataFetching.products(null, query, category, sort, exchange.tokenCookie()));
		Stream.of("card", "shop").map(indexFactory::template).forEach(i.templates()::add);
		return i;
	}
}
