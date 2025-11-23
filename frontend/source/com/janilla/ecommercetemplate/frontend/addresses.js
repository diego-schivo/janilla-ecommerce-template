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

export default class Addresses extends WebComponent {

	static get templateNames() {
		return ["addresses"];
	}

	constructor() {
		super();
	}

	connectedCallback() {
		super.connectedCallback();
		this.addEventListener("address-change", this.handleAddressChange);
		this.addEventListener("click", this.handleClick);
	}

	disconnectedCallback() {
		super.disconnectedCallback();
		this.removeEventListener("address-change", this.handleAddressChange);
		this.removeEventListener("click", this.handleClick);
	}

	async updateDisplay() {
		const s = this.state;
		const u = this.closest("app-element").state.user;
		this.appendChild(this.interpolateDom({
			$template: "",
			items: u.addresses.map(x => ({
				$template: "item",
				...x
			})),
			dialog: s.dialog ? {
				$template: "dialog",
				...s.dialog
			} : null
		}));
	}

	handleAddressChange = event => {
		event.stopPropagation();
		this.dispatchEvent(new CustomEvent("user-change", {
			bubbles: true,
			detail: { user: event.detail.address.customer }
		}));
		delete this.state.dialog;
		this.requestDisplay();
	}

	handleClick = event => {
		const b = event.target.closest("button");
		const s = this.state;
		switch (b?.name) {
			case "add":
			case "edit":
				event.stopPropagation();
				s.dialog = { id: b.value };
				this.requestDisplay();
				break;
			case "close":
				event.stopPropagation();
				delete s.dialog;
				this.requestDisplay();
				break;
		}
	}
}
