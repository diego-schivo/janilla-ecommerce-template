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

	constructor() {
		super();
	}

	connectedCallback() {
		super.connectedCallback();
		this.addEventListener("click", this.handleClick);
	}

	disconnectedCallback() {
		super.disconnectedCallback();
		this.removeEventListener("click", this.handleClick);
	}

	async updateDisplay() {
		const as = this.closest("app-element").state;
		const link = x => {
			let h;
			switch (x.type.name) {
				case "REFERENCE":
					switch (x.reference?.$type) {
						case "Page":
							h = `/${x.reference.slug}`;
							break;
						case "Product":
							h = `/products/${x.reference.slug}`;
							break;
					}
					break;
				case "CUSTOM":
					h = x.uri;
					break;
			}
			return {
				$template: "link",
				...x,
				href: h,
				target: x.newTab ? "_blank" : null
			};
		};
		this.appendChild(this.interpolateDom({
			$template: "",
			navItems: as.header?.navItems?.map(x => ({
				$template: "list-item",
				content: link(x)
			})),
			navItems2: (as.user ? [{
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
		const b = event.target.closest("button");
		if (b?.nextElementSibling?.matches("dialog"))
			b.nextElementSibling.showModal();
		else if (b?.parentElement?.matches("dialog"))
			b.parentElement.close();
	}
}
