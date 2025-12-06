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

export default class Checkout extends WebComponent {

	static get templateNames() {
		return ["checkout"];
	}

	static get observedAttributes() {
		return ["data-user-email"];
	}

	constructor() {
		super();
	}

	connectedCallback() {
		super.connectedCallback();
		this.addEventListener("change", this.handleChange);
		this.addEventListener("click", this.handleClick);
		this.addEventListener("input", this.handleInput);
		this.addEventListener("submit", this.handleSubmit);
	}

	disconnectedCallback() {
		super.disconnectedCallback();
		this.removeEventListener("change", this.handleChange);
		this.removeEventListener("click", this.handleClick);
		this.removeEventListener("input", this.handleInput);
		this.removeEventListener("submit", this.handleSubmit);
	}

	async updateDisplay() {
		const s = this.state;
		s.email ??= this.dataset.userEmail;
		const a = this.closest("app-element");
		if (s.billingAddress === undefined) {
			const aa = a.state.user.addresses;
			s.billingAddress = aa[aa.length - 1].id;
		}
		s.billingAddressSameAsShipping ??= true;
		const c = localStorage.getItem("cart");
		s.cart ??= await (await fetch(`${a.dataset.apiUrl}/carts/${c}`)).json();
		//r.updateSeo(null);
		this.appendChild(this.interpolateDom({
			$template: "",
			contact: this.dataset.userEmail ? {
				$template: "user",
				text: s.email
			} : {
				$template: "guest",
				value: s.email,
				disabled: !s.email
			},
			billingAddress: s.billingAddress ? {
				$template: "address-item",
				id: s.billingAddress,
				name: "billingAddress"
			} : {
				$template: "checkout-addresses",
				heading: "Billing address",
				description: "Please select or add your shipping and billing addresses.",
				name: "billingAddress"
			},
			billingAddressSameAsShipping: s.billingAddressSameAsShipping,
			shippingAddress: s.billingAddressSameAsShipping ? null : s.shippingAddress ? {
				$template: "address-item",
				id: s.shippingAddress,
				name: "shippingAddress"
			} : {
				$template: "checkout-addresses",
				heading: "Shipping address",
				description: "Please select a shipping address.",
				name: "shippingAddress"
			},
			payment: !s.paymentData ? { $template: "payment-trigger" } : {
				$template: "payment",
				email: s.email,
				amount: s.cart.subtotal * 100
			},
			items: s.cart.items.map(x => ({
				$template: "item",
				...x,
				image: x.product.gallery.find(y => x.variant.options.some(z => z.id === y.variantOption.id)).image,
				option: x.variant.options.map(y => y.label).join(", ")
			})),
			total: s.cart.subtotal
		}));
	}

	handleChange = event => {
		const el = event.target;
		const s = this.state;
		switch (el?.name) {
			case "billingAddress":
				s.billingAddress = parseInt(el.value);
				this.requestDisplay();
				break;
			case "billingAddressSameAsShipping":
				s.billingAddressSameAsShipping = el.checked;
				this.requestDisplay();
				break;
			case "shippingAddress":
				s.shippingAddress = parseInt(el.value);
				this.requestDisplay();
				break;
		}
	}

	handleClick = event => {
		const el = event.target;
		const s = this.state;
		switch (el?.name) {
			case "billingAddress":
				s.billingAddress = null;
				this.requestDisplay();
				break;
			case "shippingAddress":
				s.shippingAddress = null;
				this.requestDisplay();
				break;
			case "go-to-payment":
				s.paymentData = {};
				this.requestDisplay();
				break;
		}
	}

	handleInput = event => {
		if (event.target.matches('[name="email"]')) {
			this.state.email = event.target.value;
			this.requestDisplay();
		}
	}

	handleSubmit = event => {
		if (!event.target.matches("#payment-form")) {
			event.preventDefault();
			const fd = new FormData(event.target);
			this.state.email = fd.get("email");
			this.requestDisplay();
		}
	}
}
