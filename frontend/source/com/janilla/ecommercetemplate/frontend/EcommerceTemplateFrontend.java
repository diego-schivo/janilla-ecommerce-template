/*
 * MIT License
 *
 * Copyright (c) 2024-2025 Diego Schivo
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
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import javax.net.ssl.SSLContext;

import com.janilla.http.HttpClient;
import com.janilla.http.HttpHandler;
import com.janilla.http.HttpServer;
import com.janilla.ioc.DiFactory;
import com.janilla.java.Java;
import com.janilla.net.Net;
import com.janilla.web.ApplicationHandlerFactory;
import com.janilla.web.Bind;
import com.janilla.web.Invocable;
import com.janilla.web.Handle;
import com.janilla.web.NotFoundException;
import com.janilla.web.RenderableFactory;

public class EcommerceTemplateFrontend {

	public static final AtomicReference<EcommerceTemplateFrontend> INSTANCE = new AtomicReference<>();

	public static void main(String[] args) {
		try {
			EcommerceTemplateFrontend a;
			{
				var f = new DiFactory(Java.getPackageClasses(EcommerceTemplateFrontend.class.getPackageName()),
						EcommerceTemplateFrontend.INSTANCE::get);
				a = f.create(EcommerceTemplateFrontend.class,
						Java.hashMap("diFactory", f, "configurationFile",
								args.length > 0 ? Path.of(
										args[0].startsWith("~") ? System.getProperty("user.home") + args[0].substring(1)
												: args[0])
										: null));
			}

			HttpServer s;
			{
				SSLContext c;
				try (var x = Net.class.getResourceAsStream("testkeys")) {
					c = Net.getSSLContext(Map.entry("JKS", x), "passphrase".toCharArray());
				}
				var p = Integer.parseInt(a.configuration.getProperty("ecommerce-template.frontend.server.port"));
				s = a.diFactory.create(HttpServer.class,
						Map.of("sslContext", c, "endpoint", new InetSocketAddress(p), "handler", a.handler));
			}
			s.serve();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	protected final Properties configuration;

	protected final DataFetching dataFetching;

	protected final DiFactory diFactory;

	protected final HttpHandler handler;

	protected final HttpClient httpClient;

	public EcommerceTemplateFrontend(DiFactory diFactory, Path configurationFile) {
		this.diFactory = diFactory;
		if (!INSTANCE.compareAndSet(null, this))
			throw new IllegalStateException();
		configuration = diFactory.create(Properties.class, Collections.singletonMap("file", configurationFile));

		{
			var f = diFactory.create(ApplicationHandlerFactory.class, Map.of("methods",
					types().stream().flatMap(x -> Arrays.stream(x.getMethods())
							.filter(y -> !Modifier.isStatic(y.getModifiers())).map(y -> new Invocable(x, y)))
							.toList(),
					"renderableFactory", diFactory.create(RenderableFactory.class), "files",
					Stream.of("com.janilla.frontend2", EcommerceTemplateFrontend.class.getPackageName())
							.flatMap(x -> Java.getPackagePaths(x).stream().filter(Files::isRegularFile)).toList()));
			handler = x -> {
				var h = f.createHandler(Objects.requireNonNullElse(x.exception(), x.request()));
				if (h == null)
					throw new NotFoundException(x.request().getMethod() + " " + x.request().getTarget());
				return h.handle(x);
			};
		}

		{
			SSLContext c;
			try (var x = Net.class.getResourceAsStream("testkeys")) {
				c = Net.getSSLContext(Map.entry("JKS", x), "passphrase".toCharArray());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
//			httpClient = new HttpClient(c);
			httpClient = diFactory.create(HttpClient.class, Map.of("sslContext", c));
		}

		dataFetching = diFactory.create(DataFetching.class);
	}

	public Properties configuration() {
		return configuration;
	}

	public DataFetching dataFetching() {
		return dataFetching;
	}

	public DiFactory diFactory() {
		return diFactory;
	}

	public HttpHandler handler() {
		return handler;
	}

	public HttpClient httpClient() {
		return httpClient;
	}

	public Collection<Class<?>> types() {
		return diFactory.types();
	}

	@Handle(method = "GET", path = "/admin(/[\\w\\d/-]*)?")
	public Object admin(String path, CustomHttpExchange exchange) {
		IO.println("EcommerceTemplateFrontend.admin, path=" + path);
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
		return new Index(configuration.getProperty("ecommerce-template.api.url"),
				Collections.singletonMap("user", exchange.sessionUser()));
	}

	@Handle(method = "GET", path = "(/account|/account/addresses)")
	public Object account(String path, CustomHttpExchange exchange) {
		IO.println("EcommerceTemplateFrontend.account, path=" + path);
		if (exchange.sessionUser() == null)
			return URI.create("/login");
		return new Index(configuration.getProperty("ecommerce-template.api.url"), state(exchange));
	}

	@Handle(method = "GET", path = "/login")
	public Object login(CustomHttpExchange exchange) {
		IO.println("EcommerceTemplateFrontend.login");
		if (exchange.sessionUser() != null)
			return URI.create("/account");
		return new Index(configuration.getProperty("ecommerce-template.api.url"), state(exchange));
	}

	@Handle(method = "GET", path = "/logout")
	public Object logout(CustomHttpExchange exchange) {
		IO.println("EcommerceTemplateFrontend.logout");
		return new Index(configuration.getProperty("ecommerce-template.api.url"), state(exchange));
	}

	@Handle(method = "GET", path = "/([\\w\\d-]*)")
	public Index page(String slug, CustomHttpExchange exchange) {
		IO.println("EcommerceTemplateFrontend.page, slug=" + slug);
		if (slug == null || slug.isEmpty())
			slug = "home";
		var pp = dataFetching.pages(slug, exchange.tokenCookie());
		if (pp.isEmpty() && !slug.equals("home"))
			throw new NotFoundException("slug=" + slug);
		var m = state(exchange);
		m.put("page", !pp.isEmpty() ? pp.getFirst() : null);
		return new Index(configuration.getProperty("ecommerce-template.api.url"), m);
	}

	@Handle(method = "GET", path = "/shop")
	public Index shop(@Bind("q") String query, Long category, String sort, CustomHttpExchange exchange) {
		IO.println("EcommerceTemplateFrontend.shop, query=" + query + ", category=" + category);
		var m = state(exchange);
		m.put("categories", dataFetching.categories());
		m.put("products", dataFetching.products(null, query, category, sort, exchange.tokenCookie()));
		return new Index(configuration.getProperty("ecommerce-template.api.url"), m);
	}

	protected Map<String, Object> state(CustomHttpExchange exchange) {
		var x = new LinkedHashMap<String, Object>();
		x.put("user", exchange.sessionUser());
		x.put("header", dataFetching.header());
		x.put("footer", dataFetching.footer());
		x.put("enums", dataFetching.enums());
		return x;
	}
}
