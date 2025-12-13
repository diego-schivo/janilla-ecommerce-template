package com.janilla.ecommercetemplate.frontend;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public abstract class WebHandling {

	protected final Properties configuration;

	protected final DataFetching dataFetching;

	protected WebHandling(Properties configuration, DataFetching dataFetching) {
		this.configuration = configuration;
		this.dataFetching = dataFetching;
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
