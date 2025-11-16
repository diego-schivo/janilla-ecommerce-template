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

const adminRegex = /^\/admin(\/.*)?$/;
const ordersRegex = /^\/orders(\/.*)?$/;
const productRegex = /^\/products(\/.*)$/;

export default class App extends WebComponent {

	static get templateNames() {
		return ["app"];
	}

	static get observedAttributes() {
		return ["data-api-url"];
	}

	constructor() {
		super();
		if (!history.state)
			history.replaceState({}, "");
	}

	connectedCallback() {
		const el = this.children.length === 1 ? this.firstElementChild : null;
		if (el?.matches('[type="application/json"]')) {
			this.serverState = JSON.parse(el.text);
			el.remove();
		}
		super.connectedCallback();
		this.addEventListener("change", this.handleChange);
		this.addEventListener("click", this.handleClick);
		addEventListener("popstate", this.handlePopState);
		this.addEventListener("submit", this.handleSubmit);
		this.addEventListener("user-change", this.handleUserChange);
	}

	disconnectedCallback() {
		super.disconnectedCallback();
		this.removeEventListener("change", this.handleChange);
		this.removeEventListener("click", this.handleClick);
		removeEventListener("popstate", this.handlePopState);
		this.removeEventListener("submit", this.handleSubmit);
		this.removeEventListener("user-change", this.handleUserChange);
	}

	handleChange = event => {
		const el = event.target.closest("select");
		if (el?.closest("footer")) {
			if (el.value === "auto")
				localStorage.removeItem("janilla-ecommerce-template.color-scheme");
			else
				localStorage.setItem("janilla-ecommerce-template.color-scheme", el.value);
			this.requestDisplay();
		}
	}

	handleClick = event => {
		const a = event.target.closest("a");
		if (a?.href && !event.defaultPrevented && !a.target) {
			if (a.getAttribute("href") === "#") {
				event.preventDefault();
				this.querySelector("dialog").showModal();
			} else {
				const u = new URL(a.href);
				if (!u.pathname.match(adminRegex) !== !location.pathname.match(adminRegex))
					return;
				event.preventDefault();
				history.pushState({}, "", u.pathname + u.search);
				dispatchEvent(new CustomEvent("popstate"));
			}
		}
		/*
		const b = event.target.closest("button");
		if (b?.nextElementSibling?.matches("dialog"))
			b.nextElementSibling.showModal();
		else if (b?.parentElement?.matches("dialog"))
			b.parentElement.close();
		*/
	}

	handlePopState = () => {
		// console.log("handlePopState", JSON.stringify(history.state));
		delete this.serverState;
		window.scrollTo(0, 0);
		document.querySelectorAll("dialog[open]").forEach(x => x.close());
		delete this.state.notFound;
		this.requestDisplay();
	}

	handleSubmit = event => {
		if (false) {
			event.preventDefault();
			const usp = new URLSearchParams(new FormData(event.target));
			history.pushState({}, "", `/search?${usp}`);
			dispatchEvent(new CustomEvent("popstate"));
		}
	}

	handleUserChange = event => {
		if (event.detail?.user)
			this.state.user = event.detail.user;
		else
			delete this.state.user;
	}

	async updateDisplay() {
		const s = this.state;
		if (!Object.hasOwn(s, "user"))
			s.user = this.serverState && Object.hasOwn(this.serverState, "user")
				? this.serverState.user
				: await (await fetch(`${this.dataset.apiUrl}/users/me`)).json();

		const m = location.pathname.match(adminRegex);
		if (m) {
			this.appendChild(this.interpolateDom({
				$template: "",
				admin: {
					$template: "admin",
					path: m[1] ?? "/",
					apiUrl: this.dataset.apiUrl,
					userId: s.user?.id,
					adminRole: s.user?.roles?.some(x => x.name === "ADMIN")
				}
			}));
			return;
		}

		if (!Object.hasOwn(s, "header"))
			s.header = this.serverState && Object.hasOwn(this.serverState, "header")
				? this.serverState.header
				: await (await fetch(`${this.dataset.apiUrl}/header`)).json();
		if (!Object.hasOwn(s, "footer"))
			s.footer = this.serverState && Object.hasOwn(this.serverState, "footer")
				? this.serverState.footer
				: await (await fetch(`${this.dataset.apiUrl}/footer`)).json();

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
		const cs = localStorage.getItem("janilla-ecommerce-template.color-scheme");
		this.appendChild(this.interpolateDom({
			$template: "",
			style: `color-scheme: ${cs ?? "light dark"}`,
			adminBar: s.user?.roles?.some(x => x.name === "ADMIN") ? {
				$template: "admin-bar",
				userEmail: s.user?.email
			} : null,
			header: { $template: "header" },
			content: s.notFound ? { $template: "not-found" } : (() => {
				switch (location.pathname) {
					case "/account":
						return { $template: "account" };
					case "/checkout":
						return { $template: "checkout" };
					case "/login":
						return { $template: "login" };
					case "/logout":
						return { $template: "logout" };
					case "/order-confirmation":
						return {
							$template: "order-confirmation",
							stripePaymentIntentId: new URLSearchParams(location.search).get("payment_intent")
						};
				}
				const m2 = location.pathname.match(productRegex);
				if (m2)
					return {
						$template: "product",
						slug: m2[1].substring(1)
					};
				const m3 = location.pathname.match(ordersRegex);
				if (m3)
					return m3[1] ? {
						$template: "order",
						id: m3[1].substring(1)
					} : { $template: "orders" };
				return location.pathname === "/shop" ? (() => {
					const s = new URLSearchParams(location.search);
					return {
						$template: "shop",
						query: s.get("q"),
						category: s.get("category"),
						sort: s.get("sort")
					};
				})() : {
					$template: "page",
					slug: (() => {
						const s2 = location.pathname.substring(1);
						return s2 ? s2 : "home";
					})()
				};
			})(),
			footer: {
				$template: "footer",
				navItems: s.footer?.navItems?.map(link),
				options: ["auto", "light", "dark"].map(x => ({
					$template: "option",
					value: x,
					text: x.charAt(0).toUpperCase() + x.substring(1),
					selected: x === (cs ?? "auto")
				}))
			}
		}));
	}

	updateSeo(meta) {
		const sn = "Janilla Ecommerce Template";
		const t = [meta?.title && meta.title !== sn ? meta.title : null, sn].filter(x => x).join(" | ");
		const d = meta?.description ?? "";
		for (const [k, v] of Object.entries({
			title: t,
			description: d,
			"og:title": t,
			"og:description": d,
			"og:url": location.href,
			"og:site_name": sn,
			"og:image": meta?.image?.uri ? `${location.protocol}://${location.host}${meta.image.uri}` : null,
			"og:type": "website"
		}))
			if (k === "title")
				document.title = v ?? "";
		//else
		//	document.querySelector(`meta[name="${k}"]`).setAttribute("content", v ?? "");
	}

	notFound() {
		this.state.notFound = true;
		this.requestDisplay();
	}
}
