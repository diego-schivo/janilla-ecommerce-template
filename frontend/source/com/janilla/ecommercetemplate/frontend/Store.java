package com.janilla.ecommercetemplate.frontend;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import com.janilla.web.Bind;
import com.janilla.web.Handle;
import com.janilla.web.NotFoundException;

public class Store extends WebHandling {

	public Store(Properties configuration, DataFetching dataFetching) {
		super(configuration, dataFetching);
	}

	@Handle(method = "GET", path = "/checkout")
	public Object checkout(FrontendExchange exchange) {
		IO.println("Shop.checkout");
		return new Index(new Object(), configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), state(exchange));
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
