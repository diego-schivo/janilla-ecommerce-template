package com.janilla.ecommercetemplate.backend;

import com.janilla.http.HttpExchange;
import com.janilla.http.HttpHandlerFactory;
import com.janilla.web.Error;
import com.janilla.web.ExceptionHandlerFactory;
import com.janilla.web.RenderableFactory;

public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {

	protected final RenderableFactory renderableFactory;

	protected final HttpHandlerFactory rootFactory;

	public CustomExceptionHandlerFactory(RenderableFactory renderableFactory, HttpHandlerFactory rootFactory) {
		this.renderableFactory = renderableFactory;
		this.rootFactory = rootFactory;
	}

	@Override
	protected boolean handle(Error error, HttpExchange exchange) {
		super.handle(error, exchange);
		var r = renderableFactory.createRenderable(null, exchange.exception().getMessage());
		var h = rootFactory.createHandler(r);
		return h.handle(exchange);
	}
}
