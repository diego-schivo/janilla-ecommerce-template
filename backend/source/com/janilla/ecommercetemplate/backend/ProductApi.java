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
package com.janilla.ecommercetemplate.backend;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.janilla.cms.CollectionApi;
import com.janilla.http.HttpExchange;
import com.janilla.persistence.Persistence;
import com.janilla.web.Bind;
import com.janilla.web.Handle;

@Handle(path = "/api/products")
public class ProductApi extends CollectionApi<Long, Product> {

	public static final AtomicReference<ProductApi> INSTANCE = new AtomicReference<>();

	public ProductApi(Predicate<HttpExchange> drafts, Persistence persistence) {
		super(Product.class, drafts, persistence);
		if (!INSTANCE.compareAndSet(null, this))
			throw new IllegalStateException();
	}

	@Override
	public List<Product> read(Long skip, Long limit) {
		throw new UnsupportedOperationException();
	}

	@Handle(method = "GET")
	public List<Product> read(String slug, @Bind("q") String query, @Bind("category") Long[] categories, String sort,
			HttpExchange exchange) {
//		IO.println(
//				"ProductApi.read, slug=" + slug + ", query=" + query + ", categories=" + categories + ", sort=" + sort);
		Stream<Product> pp;
		{
			var d = drafts.test(exchange);
			var m = new LinkedHashMap<String, Object[]>();
			if (slug != null && !slug.isBlank())
				m.put(d ? "slugDraft" : "slug", new Object[] { slug });
			if (categories != null && categories.length != 0)
				m.put("categories", categories);
			pp = crud().read(!m.isEmpty() ? crud().filter(m, 0, -1).ids() : crud().list(), d).stream();
		}
		pp = query != null && !query.isBlank() ? pp.filter(x -> {
			var m = x.meta();
			var s = Stream.of(m != null ? m.title() : null, m != null ? m.description() : null)
					.filter(y -> y != null && !y.isBlank()).collect(Collectors.joining(" "));
			return s.toLowerCase().contains(query.toLowerCase());
		}) : pp;
		switch (Objects.requireNonNullElse(sort, "")) {
		case "":
			pp = pp.sorted(Comparator.comparing(x -> x.title()));
			break;
		case "-createdAt":
			pp = pp.sorted(Comparator.comparing((Product x) -> x.createdAt()).reversed());
			break;
		case "priceInUSD":
			pp = pp.sorted(Comparator.comparing(x -> x.price()));
			break;
		case "-priceInUSD":
			pp = pp.sorted(Comparator.comparing((Product x) -> x.price()).reversed());
			break;
		}
		return pp.toList();
	}
}
