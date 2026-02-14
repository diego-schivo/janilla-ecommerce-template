/*
 * MIT License
 *
 * Copyright (c) 2018-2025 Payload CMS, Inc. <info@payloadcms.com>
 * Copyright (c) 2024-2026 Diego Schivo <diego.schivo@janilla.com>
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.janilla.backend.cms.CollectionApi;
import com.janilla.backend.cms.UserHttpExchange;
import com.janilla.backend.persistence.Persistence;
import com.janilla.http.HttpExchange;
import com.janilla.web.ForbiddenException;
import com.janilla.web.Handle;
import com.janilla.web.UnauthorizedException;

@Handle(path = "/api/orders")
public class OrderApi extends CollectionApi<Long, Order> {

	public OrderApi(Predicate<HttpExchange> drafts, Persistence persistence) {
		super(Order.class, drafts, persistence);
	}

	@Handle(method = "GET")
	public List<Order> read(Long customer, UserHttpExchange<UserImpl> exchange) {
		var u = (UserImpl) exchange.sessionUser();
		if (u == null || !(u.hasRole(UserRoleImpl.ADMIN) || u.hasRole(UserRoleImpl.CUSTOMER)))
			throw new UnauthorizedException();

		if (u.hasRole(UserRoleImpl.CUSTOMER)) {
			if (customer == null)
				customer = u.id();
			else if (!customer.equals(u.id()))
				throw new ForbiddenException();
		}

		var oo = new ArrayList<>(
				crud().read(customer != null ? crud().filter("customer", new Object[] { customer }) : crud().list(),
						drafts.test(exchange)));
		Collections.reverse(oo);
		return oo;
	}

	@Override
	public Order read(Long id, HttpExchange exchange) {
		@SuppressWarnings("unchecked")
		var u = ((UserHttpExchange<UserImpl>) exchange).sessionUser();
		if (u == null || !(u.hasRole(UserRoleImpl.ADMIN) || u.hasRole(UserRoleImpl.CUSTOMER)))
			throw new UnauthorizedException();

		var o = super.read(id, exchange);
		if (u.hasRole(UserRoleImpl.CUSTOMER) && !u.id().equals(o.customer()))
			throw new ForbiddenException();
		return o;
	}
}
