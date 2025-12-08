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

import java.util.Properties;

import com.janilla.persistence.Persistence;
import com.janilla.web.Handle;

public abstract class PaymentApi {

	protected final Properties configuration;

	protected final Persistence persistence;

	protected PaymentApi(Properties configuration, Persistence persistence) {
		this.configuration = configuration;
		this.persistence = persistence;
	}

	public record InitiateData(String email, Long cart, AddressData billingAddress, AddressData shippingAddress) {
	}

	public record InitiateResult(String paymentIntent, String clientSecret) {
	}

	@Handle(method = "POST", path = "initiate")
	public InitiateResult initiate(InitiateData data, BackendExchange exchange) {
		var u = exchange.sessionUser();
		return initiate(u, u != null ? u.email() : data.email(),
				persistence.crud(Cart.class).read(u != null ? u.carts().getFirst() : data.cart()),
				data.billingAddress(), data.shippingAddress());
	}

	public record ConfirmOrderData(String email, String paymentIntent) {
	}

	public record ConfirmOrderResult(Long order, Long transaction) {
	}

	@Handle(method = "POST", path = "confirm-order")
	public ConfirmOrderResult confirmOrder(ConfirmOrderData data, BackendExchange exchange) {
		var u = exchange.sessionUser();
		return confirmOrder(u, u != null ? u.email() : data.email(), data.paymentIntent());
	}

	protected abstract InitiateResult initiate(User user, String email, Cart cart, AddressData billingAddress,
			AddressData shippingAddress);

	protected abstract ConfirmOrderResult confirmOrder(User user, String email, String paymentIntent);
}
