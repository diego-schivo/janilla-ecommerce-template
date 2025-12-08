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
import WebComponent from "./web-component.js";

export default class Payment extends WebComponent {

	static get templateNames() {
		return ["payment"];
	}

	static get observedAttributes() {
		return ["data-email", "data-amount"];
	}

	constructor() {
		super();
	}

	connectedCallback() {
		super.connectedCallback();
		this.addEventListener("submit", this.handleSubmit);
	}

	disconnectedCallback() {
		super.disconnectedCallback();
		this.removeEventListener("submit", this.handleSubmit);
	}

	async updateDisplay() {
		this.appendChild(this.interpolateDom({ $template: "" }));
		if (!this.state.elements) {
			const a = this.closest("app-element");
			const c = this.closest("checkout-element");
			const j = await (await fetch(`${a.dataset.apiUrl}/payments/stripe/initiate`, {
				method: "POST",
				headers: { "content-type": "application/json" },
				body: JSON.stringify({
					email: this.dataset.email,
					cart: c.state.cart.id,
					billingAddress: a.state.user.addresses.find(x => x.id === c.state.billingAddress),
					shippingAddress: a.state.user.addresses.find(x => x.id === c.state.shippingAddress)
				})
			})).json();
			this.state.elements = a.state.stripe.elements({
				appearance: {
					theme: "stripe",
				},
				clientSecret: j.clientSecret
				//loader: "auto"
			});
			this.state.elements.create("payment", { layout: "accordion" }).mount("#payment-element");
		}
	}

	handleSubmit = async event => {
		event.preventDefault();
		const a = this.closest("app-element");
		const c = this.closest("checkout-element");
		const ba = a.state.user.addresses.find(x => x.id === c.state.billingAddress);
		let j = await a.state.stripe.confirmPayment({
			confirmParams: {
				return_url: `${location.origin}/order-confirmation`,
				payment_method_data: {
					billing_details: {
						email: a.state.user.email,
						phone: ba?.phone,
						address: {
							line1: ba?.addressLine1,
							line2: ba?.addressLine2,
							city: ba?.city,
							state: ba?.state,
							postal_code: ba?.postalCode,
							country: ba?.country,
						},
					},
				}
			},
			elements: this.state.elements,
			redirect: "if_required"
		});
		if (j.paymentIntent?.status === "succeeded") {
			j = await (await fetch(`${a.dataset.apiUrl}/payments/stripe/confirm-order`, {
				method: "POST",
				headers: { "content-type": "application/json" },
				body: JSON.stringify({
					email: this.dataset.email,
					paymentIntent: j.paymentIntent.id
				})
			})).json();
			if (j?.order) {
				await fetch(`${a.dataset.apiUrl}/carts/${c.state.cart.id}`, { method: "DELETE" });
				localStorage.removeItem("cart");
				const u = new URL(`/orders/${j.order}`, location.href);
				if (this.dataset.email)
					u.searchParams.append("email", this.dataset.email);
				history.pushState({}, "", u.pathname + u.search);
				dispatchEvent(new CustomEvent("popstate"));
			}
		}
	}
}
