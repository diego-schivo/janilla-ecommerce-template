package com.janilla.ecommercetemplate.frontend;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.janilla.web.Bind;
import com.janilla.web.Handle;
import com.janilla.web.NotFoundException;

public class WebHandling {

	protected final Properties configuration;

	protected final DataFetching dataFetching;

	public WebHandling(Properties configuration, DataFetching dataFetching) {
		this.configuration = configuration;
		this.dataFetching = dataFetching;
	}

	@Handle(method = "GET", path = "/account")
	public Object account(FrontendExchange exchange) {
		IO.println("Account.account");
		if (exchange.sessionUser() == null)
			return URI.create("/login");
		var oo = dataFetching.orders(exchange.tokenCookie());
		var s = state(exchange);
		s.put("orders", oo);
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), s);
	}

	@Handle(method = "GET", path = "/account/addresses")
	public Object addresses(FrontendExchange exchange) {
		IO.println("Account.addresses");
		if (exchange.sessionUser() == null)
			return URI.create("/login");
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), state(exchange));
	}

	@Handle(method = "GET", path = "/admin(/[\\w\\d/-]*)?")
	public Object admin(String path, FrontendExchange exchange) {
		IO.println("Admin.admin, path=" + path);
		if (path == null || path.isEmpty())
			path = "/";
		switch (path) {
		case "/":
			if (exchange.sessionUser() == null)
				return URI.create("/admin/login");
			break;
		case "/login":
			if (((List<?>) dataFetching.users(0l, 1l)).isEmpty())
				return URI.create("/admin/create-first-user");
			break;
		}
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"),
				Collections.singletonMap("user", exchange.sessionUser()));
	}

	@Handle(method = "GET", path = "/checkout")
	public Object checkout(FrontendExchange exchange) {
		IO.println("Shop.checkout");
		return new Index(new Object(), configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), state(exchange));
	}

	@Handle(method = "GET", path = "/login")
	public Object login(FrontendExchange exchange) {
		IO.println("Account.login");
		if (exchange.sessionUser() != null)
			return URI.create("/account");
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), state(exchange));
	}

	@Handle(method = "GET", path = "/logout")
	public Object logout(FrontendExchange exchange) {
		IO.println("Account.logout");
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), state(exchange));
	}

	@Handle(method = "GET", path = "/orders/(\\d+)")
	public Object order(Long id, FrontendExchange exchange) {
		IO.println("Account.order, id=" + id);
		var o = dataFetching.order(id, exchange.tokenCookie());
		if (o == null)
			throw new NotFoundException("id=" + id);
		var s = state(exchange);
		s.put("order", o);
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), s);
	}

	@Handle(method = "GET", path = "/orders")
	public Object orders(FrontendExchange exchange) {
		IO.println("Account.orders");
		var oo = dataFetching.orders(exchange.tokenCookie());
		var s = state(exchange);
		s.put("orders", oo);
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), s);
	}

	@Handle(method = "GET", path = "/([\\w\\d-]*)")
	public Object page(String slug, FrontendExchange exchange) {
		IO.println("Shop.page, slug=" + slug);
		if (slug == null || slug.isEmpty())
			slug = "home";
		var pp = dataFetching.pages(slug, exchange.tokenCookie());
		if (pp.isEmpty() && !slug.equals("home"))
			throw new NotFoundException("slug=" + slug);
		var s = state(exchange);
		s.put("page", !pp.isEmpty() ? pp.getFirst() : null);
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), s);
	}

	@Handle(method = "GET", path = "/products/([\\w\\d-]+)")
	public Object product(String slug, FrontendExchange exchange) {
		IO.println("Shop.product, slug=" + slug);
		var pp = dataFetching.products(slug, null, null, null, exchange.tokenCookie());
		if (pp.isEmpty())
			throw new NotFoundException("slug=" + slug);
		var s = state(exchange);
		s.put("product", pp.getFirst());
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), s);
	}

	@Handle(method = "GET", path = "/shop")
	public Object shop(@Bind("q") String query, Long category, String sort, FrontendExchange exchange) {
		IO.println("Shop.shop, query=" + query + ", category=" + category);
		var s = state(exchange);
		s.put("categories", dataFetching.categories());
		s.put("products", dataFetching.products(null, query, category, sort, exchange.tokenCookie()));
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), s);
	}

	protected Map<String, Object> state(FrontendExchange exchange) {
		var x = new LinkedHashMap<String, Object>();
		x.put("user", exchange.sessionUser());
		x.put("header", dataFetching.header());
		x.put("footer", dataFetching.footer());
		x.put("enums", dataFetching.enums());
		return x;
	}
}
