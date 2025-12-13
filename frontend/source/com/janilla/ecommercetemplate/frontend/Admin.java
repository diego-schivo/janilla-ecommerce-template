package com.janilla.ecommercetemplate.frontend;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.janilla.web.Handle;

public class Admin extends WebHandling {

	public Admin(Properties configuration, DataFetching dataFetching) {
		super(configuration, dataFetching);
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
}
