package com.janilla.ecommercetemplate.frontend;

import com.janilla.http.HttpExchange;
import com.janilla.http.HttpHandlerFactory;
import com.janilla.web.Error;
import com.janilla.web.ExceptionHandlerFactory;
import com.janilla.web.RenderableFactory;

public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {

	protected final IndexFactory indexFactory;

	protected final RenderableFactory renderableFactory;

	protected final HttpHandlerFactory rootFactory;

	public CustomExceptionHandlerFactory(IndexFactory indexFactory, RenderableFactory renderableFactory,
			HttpHandlerFactory rootFactory) {
		this.indexFactory = indexFactory;
		this.renderableFactory = renderableFactory;
		this.rootFactory = rootFactory;
	}

	@Override
	protected boolean handle(Error error, HttpExchange exchange) {
		IO.println(
				"CustomExceptionHandlerFactory.handle, " + exchange.request().getPath() + ", " + exchange.exception());
		super.handle(error, exchange);
		var i = indexFactory.index((FrontendExchange) exchange);
		i.state().put("error", error);
		var r = renderableFactory.createRenderable(null, i);
		var h = rootFactory.createHandler(r);
		return h.handle(exchange);
	}
}
