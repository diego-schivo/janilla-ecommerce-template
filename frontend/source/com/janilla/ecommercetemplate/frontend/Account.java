package com.janilla.ecommercetemplate.frontend;

import java.net.URI;
import java.util.Properties;

import com.janilla.web.Handle;
import com.janilla.web.NotFoundException;

public class Account extends WebHandling {

	public Account(Properties configuration, DataFetching dataFetching) {
		super(configuration, dataFetching);
	}

	@Handle(method = "GET", path = "(/account|/account/addresses)")
	public Object account(String path, FrontendExchange exchange) {
		IO.println("Account.account, path=" + path);
		if (exchange.sessionUser() == null)
			return URI.create("/login");
		return new Index(null, configuration.getProperty("ecommerce-template.api.url"),
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
}
