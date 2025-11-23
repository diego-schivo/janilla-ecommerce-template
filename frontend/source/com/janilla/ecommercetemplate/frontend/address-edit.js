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

export default class AddressEdit extends WebComponent {

	static get templateNames() {
		return ["address-edit"];
	}

	static get observedAttributes() {
		return ["data-id"];
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
		const a = this.closest("app-element");
		const u = a.state.user;
		const a2 = this.dataset.id ? u.addresses.find(x => x.id == this.dataset.id) : { customer: u.id };
		this.appendChild(this.interpolateDom({
			$template: "",
			title: a2.id ? "Edit address" : "Add a new address",
			form: {
				$template: "form",
				...a2,
				titleValues: a.state.enums["Title"],
				countryValues: a.state.enums["Country"]
			}
		}));
	}

	handleSubmit = async event => {
		event.preventDefault();
		const a = this.closest("app-element");
		const o = {
			customer: a.state.user.id,
			...Object.fromEntries(new FormData(event.target))
		};
		const r = await fetch(`${a.dataset.apiUrl}/addresses${this.dataset.id ? `/${this.dataset.id}` : ""}`, {
			method: this.dataset.id ? "PUT" : "POST",
			headers: { "content-type": "application/json" },
			body: JSON.stringify(o)
		});
		if (r.ok)
			this.dispatchEvent(new CustomEvent("address-change", {
				bubbles: true,
				detail: { address: await r.json() }
			}));
	}
}
