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

export default class MobileMenu extends WebComponent {

	static get templateNames() {
		return ["mobile-menu"];
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
		this.closest("app-element").addEventListener("userchanged", this.handleUserChanged);
	}

	disconnectedCallback() {
		super.disconnectedCallback();
		this.removeEventListener("click", this.handleClick);
		this.closest("app-element").removeEventListener("userchanged", this.handleUserChanged);
	}

	async updateDisplay() {
		const a = this.closest("app-element");
		this.appendChild(this.interpolateDom({
			$template: "",
			navItems: a.state.header?.navItems?.map(x => ({
				$template: "list-item",
				content: {
					$template: "link",
					...x,
					document: x.type.name === "REFERENCE" ? `${x.document.$type}:${x.document.slug}` : null,
					href: x.type.name === "CUSTOM" ? x.uri : null,
					target: x.newTab ? "_blank" : null
				}
			})),
			navItems2: (a.state.user ? [{
				href: "/orders",
				text: "Orders"
			}, {
				href: "/account/addresses",
				text: "Addresses"
			}, {
				href: "/account",
				text: "Manage account"
			}, {
				href: "/logout",
				class: "button secondary",
				text: "Log out"
			}] : [{
				href: "/login",
				class: "button",
				text: "Log in"
			}]).map(x => ({
				$template: "list-item",
				content: {
					$template: "link",
					...x
				}
			}))
		}));
	}

	handleClick = event => {
		const x = event.target.closest("button");
		switch (x?.name) {
			case "show":
				this.querySelector("dialog").showModal();
				break;
			case "close":
				this.querySelector("dialog").close();
				break;
		}
	}

	handleUserChanged = () => {
	    this.requestDisplay();
	}
}
