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

export default class CartModal extends WebComponent {

	static get templateNames() {
		return ["cart-modal"];
	}

	static get observedAttributes() {
		return [];
	}

	constructor() {
		super();
	}

	connectedCallback() {
		super.connectedCallback();
		this.addEventListener("click", this.handleClick);
		this.addEventListener("submit", this.handleSubmit);
	}

	disconnectedCallback() {
		super.disconnectedCallback();
		this.removeEventListener("click", this.handleClick);
		this.removeEventListener("submit", this.handleSubmit);
	}

	async updateDisplay() {
		const s = this.state;
		const c = localStorage.getItem("cart");
		s.cart = (c ? await (await fetch(`${this.closest("app-element").dataset.apiUrl}/carts/${c}`)).json() : null) ?? {};
		if (c && !s.cart.id)
			localStorage.removeItem("cart");
		const q = s.cart.items?.reduce((x, y) => x + y.quantity, 0) ?? 0;
		this.appendChild(this.interpolateDom({
			$template: "",
			quantity: q !== 0 ? {
				$template: "quantity",
				text: q
			} : null,
			content: q !== 0 ? {
				$template: "content",
				items: s.cart.items.map(x => ({
					$template: "item",
					...x,
					image: x.product.gallery.find(y => x.variant.options.some(z => z.id === y.variantOption.id)).image,
					option: x.variant.options.map(y => y.label).join(", "),
					quantityMinus1: x.quantity - 1,
					quantityPlus1: x.quantity + 1,
				}))
			} : { $template: "empty-content" },
			footer: q !== 0 ? {
				$template: "footer",
				total: s.cart.subtotal
			} : null
		}));
		const d = this.querySelector("dialog");
		if (s.dialog)
			d.showModal();
		else
			d.close();
	}

	handleClick = event => {
		const b = event.target.closest("button");
		const s = this.state;
		switch (b?.name) {
			case "open":
				s.dialog = true;
				this.requestDisplay();
				break;
			case "close":
				delete s.dialog;
				this.requestDisplay();
				break;
		}
	}

	handleSubmit = async event => {
		event.preventDefault();
		const s = this.state;
		const fd = new FormData(event.target);
		const p = parseInt(fd.get("product"));
		const v = parseInt(fd.get("variant"));
		const q = parseInt(fd.get("quantity"));
		const r = await fetch(`${this.closest("app-element").dataset.apiUrl}/carts/${s.cart.id}`, {
			method: "PATCH",
			headers: { "content-type": "application/json" },
			body: JSON.stringify({
				items: s.cart.items.map(x => ({
					$type: "CartItem",
					product: x.product.id,
					variant: x.variant.id,
					quantity: x.product.id == p && x.variant.id == v ? q : x.quantity
				})).filter(x => x.quantity)
			})
		});
		s.cart = await r.json();
		/*
		this.dispatchEvent(new CustomEvent("cart-change", {
			bubbles: true,
			detail: j
		}));
		*/
		this.requestDisplay();
	}
}
