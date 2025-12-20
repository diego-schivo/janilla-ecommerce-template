package com.janilla.ecommercetemplate.frontend;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import com.janilla.admin.frontend.AdminFrontend;
import com.janilla.frontend.Frontend;

public class IndexFactory {

	protected final Properties configuration;

	protected final DataFetching dataFetching;

	public IndexFactory(Properties configuration, DataFetching dataFetching) {
		this.configuration = configuration;
		this.dataFetching = dataFetching;
	}

	public Index index(FrontendExchange exchange) {
		return new Index(null, imports(), configuration.getProperty("ecommerce-template.api.url"),
				configuration.getProperty("ecommerce-template.stripe.publishable-key"), state(exchange));
	}

	protected Map<String, Object> state(FrontendExchange exchange) {
		var x = new LinkedHashMap<String, Object>();
		x.put("user", exchange.sessionUser());
		x.put("header", dataFetching.header());
		x.put("footer", dataFetching.footer());
		x.put("enums", dataFetching.enums());
		return x;
	}

	protected Map<String, String> imports() {
		var m = new LinkedHashMap<String, String>();
		Frontend.putImports(m);
		AdminFrontend.putImports(m);
		Stream.of("admin", "admin-fields").forEach(x -> m.put(x, "/custom-" + x + ".js"));
		Stream.of("account", "address-edit", "address-item", "addresses", "app", "call-to-action", "card", "cart-modal",
				"checkout", "checkout-addresses", "content", "create-account", "create-address-modal", "find-order",
				"header", "hero", "intl-format", "link", "login", "logout", "lucide-icon", "media-block", "message",
				"mobile-menu", "not-found", "order", "order-confirmation", "order-item", "orders", "page", "payment",
				"price", "product", "product-description", "product-gallery", "product-item", "rich-text", "select",
				"shop", "toaster", "variant-selector").forEach(x -> m.put(x, "/" + x + ".js"));
		return m;
	}
}
