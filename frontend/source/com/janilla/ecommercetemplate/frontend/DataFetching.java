/*
 * MIT License
 *
 * Copyright (c) 2018-2025 Payload CMS, Inc. <info@payloadcms.com>
 * Copyright (c) 2024-2025 Diego Schivo <diego.schivo@janilla.com>
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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.janilla.http.HttpClient;
import com.janilla.http.HttpCookie;
import com.janilla.net.UriQueryBuilder;

public class DataFetching {

	protected final Properties configuration;

	protected final HttpClient httpClient;

	public DataFetching(Properties configuration, HttpClient httpClient) {
		this.configuration = configuration;
		this.httpClient = httpClient;
	}

	public List<?> categories() {
		return (List<?>) httpClient
				.getJson(URI.create(configuration.getProperty("ecommerce-template.api.url") + "/categories"));
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<String>> enums() {
		return (Map<String, List<String>>) httpClient
				.getJson(URI.create(configuration.getProperty("ecommerce-template.api.url") + "/enums"));
	}

	public Object footer() {
		return httpClient.getJson(URI.create(configuration.getProperty("ecommerce-template.api.url") + "/footer"));
	}

	public Object header() {
		return httpClient.getJson(URI.create(configuration.getProperty("ecommerce-template.api.url") + "/header"));
	}

	public Object order(Long id, HttpCookie token) {
		return httpClient.getJson(URI.create(configuration.getProperty("ecommerce-template.api.url") + "/orders/" + id),
				token != null ? token.format() : null);
	}

	public List<?> orders(HttpCookie token) {
		return (List<?>) httpClient.getJson(
				URI.create(configuration.getProperty("ecommerce-template.api.url") + "/orders"),
				token != null ? token.format() : null);
	}

	public List<?> pages(String slug, HttpCookie token) {
		return (List<?>) httpClient.getJson(
				slug != null
						? URI.create(configuration.getProperty("ecommerce-template.api.url") + "/pages?"
								+ new UriQueryBuilder().append("slug", slug))
						: URI.create(configuration.getProperty("ecommerce-template.api.url") + "/pages"),
				token != null ? token.format() : null);
	}

	public List<?> products(String slug, String query, Long category, String sort, HttpCookie token) {
		return (List<?>) httpClient.getJson(URI.create(configuration.getProperty("ecommerce-template.api.url")
				+ "/products?"
				+ new UriQueryBuilder().append("slug", slug).append("q", query)
						.append("category", category != null ? category.toString() : null).append("sort", sort)),
				token != null ? token.format() : null);
	}

	public Object sessionUser(HttpCookie token) {
		return httpClient.getJson(URI.create(configuration.getProperty("ecommerce-template.api.url") + "/users/me"),
				token != null ? token.format() : null);
	}

	public List<?> users(Long skip, Long limit) {
		return (List<?>) httpClient.getJson(URI.create(configuration.getProperty("ecommerce-template.api.url")
				+ "/users?" + new UriQueryBuilder().append("skip", skip != null ? skip.toString() : null)
						.append("limit", limit != null ? limit.toString() : null)));
	}
}
