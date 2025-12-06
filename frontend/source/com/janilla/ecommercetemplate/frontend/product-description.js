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

export default class ProductDescription extends WebComponent {

	static get templateNames() {
		return ["product-description"];
	}

	static get observedAttributes() {
		return ["data-variant"];
	}

	constructor() {
		super();
	}

	connectedCallback() {
		super.connectedCallback();
		//this.addEventListener("change", this.handleChange);
		this.addEventListener("submit", this.handleSubmit);
	}

	disconnectedCallback() {
		super.disconnectedCallback();
		//this.removeEventListener("change", this.handleChange);
		this.removeEventListener("submit", this.handleSubmit);
	}

	async updateDisplay() {
		const pe = this.closest("product-element");
		const p = pe.state.product;
		const aa = p.enableVariants ? p.variants.map(x => x.priceInUsd) : [p.priceInUsd];
		const la = aa ? Math.min(...aa) : null;
		const ha = aa ? Math.max(...aa) : null;
		const pp = new URLSearchParams(pe.dataset.search);
		this.appendChild(this.interpolateDom({
			$template: "",
			...p,
			price: {
				$template: "price",
				...(la === ha ? { amount: la } : {
					lowestAmount: la,
					highestAmount: ha
				})
			},
			variantSelector: p.enableVariants ? {
				$template: "variant-selector",
				options: p.variantTypes.map(x => pp.get(x.name)).filter(x => x)
			} : null,
			disabled: !this.dataset.variant
		}));
	}

	/*
	handleChange = event => {
		const p = this.closest("product-element").state.product;
		const fd = new FormData(event.target.form);
		this.state.variant = p.variants.find(x => x.options.every(y => y.id == fd.get(y.type.name)));
		this.requestDisplay();
	}
	*/

	handleSubmit = async event => {
		event.preventDefault();
		const p = this.closest("product-element").state.product;
		const v = p.variants.find(x => x.id == this.dataset.variant);
		const u = new URL(`${this.closest("app-element").dataset.apiUrl}/carts`, location.href);
		const c = localStorage.getItem("cart");
		const o = c ? await (await fetch(`${u}/${c}`)).json() : null;
		const ii = o ? o.items.map(x => ({
			...x,
			product: x.product.id,
			variant: x.variant.id
		})) : [];
		const i = ii.findIndex(x => x.product === p.id && x.variant === v.id);
		if (i !== -1)
			ii[i].quantity++;
		else
			ii.push({
				$type: "CartItem",
				product: p.id,
				variant: v.id,
				quantity: 1
			});
		const r = await fetch(o ? `${u}/${c}` : u, {
			method: o ? "PATCH" : "POST",
			headers: { "content-type": "application/json" },
			body: JSON.stringify(o ? { items: ii } : {
				$type: "Cart",
				items: ii,
				currency: "USD"
			})
		});
		const j = await r.json();
		if (!o)
			localStorage.setItem("cart", j.id);
		//this.dispatchEvent(new CustomEvent("cart-change", { bubbles: true }));
	}
}
