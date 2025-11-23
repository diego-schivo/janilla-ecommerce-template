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

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.janilla.http.HttpClient;
import com.janilla.http.HttpCookie;
import com.janilla.net.Net;

public class DataFetching {

	protected final Properties configuration;

	protected final HttpClient httpClient;

	public DataFetching(Properties configuration, HttpClient httpClient) {
		this.configuration = configuration;
		this.httpClient = httpClient;
	}

	public List<?> categories() {
		return (List<?>) httpClient.getJson(uri("/categories"));
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<String>> enums() {
		return (Map<String, List<String>>) httpClient.getJson(uri("/enums"));
	}

	public Object footer() {
		return httpClient.getJson(uri("/footer"));
	}

	public Object header() {
		return httpClient.getJson(uri("/header"));
	}

	public List<?> pages(String slug, HttpCookie token) {
		return (List<?>) httpClient.getJson(slug != null ? uri("/pages", "slug", slug) : uri("/pages"),
				token != null ? token.format() : null);
	}

	public List<?> products(String slug, String query, Long category, String sort, HttpCookie token) {
		return (List<?>) httpClient.getJson(
				uri("/products", "slug", slug, "q", query, "category", category, "sort", sort),
				token != null ? token.format() : null);
	}

	public Object sessionUser(HttpCookie token) {
		return httpClient.getJson(uri("/users/me"), token != null ? token.format() : null);
	}

	public List<?> users(Long skip, Long limit) {
		return (List<?>) httpClient.getJson(uri("/users", "skip", skip, "limit", limit));
	}

	protected URI uri(String path) {
		return uri(path, (Object[][]) null);
	}

	protected URI uri(String path, String name, Object value) {
		return uri(path, new Object[] { name, value });
	}

	protected URI uri(String path, String name1, Object value1, String name2, Object value2) {
		return uri(path, new Object[] { name1, value1 }, new Object[] { name2, value2 });
	}

	protected URI uri(String path, String name1, Object value1, String name2, Object value2, String name3,
			Object value3) {
		return uri(path, new Object[] { name1, value1 }, new Object[] { name2, value2 },
				new Object[] { name3, value3 });
	}

	protected URI uri(String path, String name1, Object value1, String name2, Object value2, String name3,
			Object value3, String name4, Object value4) {
		return uri(path, new Object[] { name1, value1 }, new Object[] { name2, value2 }, new Object[] { name3, value3 },
				new Object[] { name4, value4 });
	}

	protected URI uri(String path, Object[]... pairs) {
		var s = pairs != null ? Arrays.stream(pairs).filter(x -> x[1] != null)
				.map(x -> Net.urlEncode(x[0].toString()) + "=" + Net.urlEncode(x[1].toString()))
				.collect(Collectors.joining("&")) : null;
		var b = new StringBuilder().append(URI.create(configuration.getProperty("ecommerce-template.api.url")))
				.append(path);
		if (s != null && !s.isEmpty())
			b.append('?').append(s);
		return URI.create(b.toString());
	}
}
