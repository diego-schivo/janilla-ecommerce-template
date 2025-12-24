/*
 * MIT License
 *
 * Copyright (c) 2018-2025 Payload CMS, Inc. <info@payloadcms.com>
 * Copyright (c) 2024-2025 Diego Schivo <diego.schivo@janilla.com>
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
import WebComponent from "web-component";

const adminRegex = /^\/admin(\/.*)?$/;
const ordersRegex = /^\/orders(\/.*)?$/;
const productRegex = /^\/products(\/.*)$/;

export default class App extends WebComponent {

    static get templateNames() {
        return ["app"];
    }

    static get observedAttributes() {
        return ["data-api-url", "data-stripe-publishable-key"];
    }

    constructor() {
        super();
        if (!history.state)
            history.replaceState({}, "");
    }

    get user() {
        return this.state.user;
    }

    set user(user) {
        this.state.user = user;
        this.dispatchEvent(new CustomEvent("userchanged", { detail: user }));
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
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        this.removeEventListener("change", this.handleChange);
        this.removeEventListener("click", this.handleClick);
        removeEventListener("popstate", this.handlePopState);
        this.removeEventListener("submit", this.handleSubmit);
    }

    async updateDisplay() {
        const s = this.state;
        const ss = this.serverState;

        if (ss?.error?.code === 404)
            s.notFound = true;

        if (!Object.hasOwn(s, "user"))
            s.user = ss && Object.hasOwn(ss, "user")
                ? ss.user
                : await (await fetch(`${this.dataset.apiUrl}/users/me`)).json();

        const p = location.pathname;
        const spp = new URLSearchParams(location.search);
        const u0 = s.user;

        if (s.user)
            switch (p) {
                case "/create-account":
                case "/login":
                    const u = new URL("/account", location.href);
                    u.searchParams.append("warning", "You are already logged in.");
                    this.navigate(u);
                    return;
                case "/logout":
                    await fetch(`${this.dataset.apiUrl}/users/logout`, { method: "POST" });
                    s.user = null;
                    break;
            }
        else if (["/account"].includes(p) || p.startsWith("/account/")) {
            this.navigate(new URL("/login", location.href));
            return;
        }

        const m = p.match(adminRegex);
        if (m) {
            this.appendChild(this.interpolateDom({
                $template: "",
                admin: {
                    $template: "admin",
                    path: m[1] ?? "/",
                    apiUrl: this.dataset.apiUrl,
                    userId: s.user?.id,
                    userAdmin: s.user?.roles?.some(x => x.name === "ADMIN")
                }
            }));
            return;
        }

        if (!Object.hasOwn(s, "header"))
            s.header = ss && Object.hasOwn(ss, "header")
                ? ss.header
                : await (await fetch(`${this.dataset.apiUrl}/header`)).json();
        if (!Object.hasOwn(s, "footer"))
            s.footer = ss && Object.hasOwn(ss, "footer")
                ? ss.footer
                : await (await fetch(`${this.dataset.apiUrl}/footer`)).json();
        if (!Object.hasOwn(s, "enums"))
            s.enums = ss && Object.hasOwn(ss, "enums")
                ? ss.enums
                : await (await fetch(`${this.dataset.apiUrl}/enums`)).json();

        const cs = localStorage.getItem("janilla-ecommerce-template.color-scheme");
        this.appendChild(this.interpolateDom({
            $template: "",
            public: {
                $template: "public",
                colorScheme: cs ?? "light dark",
                adminBar: s.user?.roles?.some(x => x.name === "ADMIN") ? {
                    $template: "admin-bar",
                    userEmail: s.user?.email
                } : null,
                content: s.notFound ? { $template: "not-found" } : (() => {
                    switch (p) {
                        case "/account":
                            return {
                                $template: "account",
                                success: spp.get("success"),
                                warning: spp.get("warning")
                            };
                        case "/account/addresses":
                            return { $template: "addresses" };
                        case "/checkout":
                            if (!Array.from(document.head.querySelectorAll("script"))
                                .some(x => x.src === "https://js.stripe.com/clover/stripe.js")) {
                                const el = document.createElement("script");
                                el.onload = () => {
                                    if (!s.stripe && typeof Stripe !== "undefined")
                                        s.stripe = Stripe(this.dataset.stripePublishableKey);
                                }
                                document.head.append(el);
                                el.src = "https://js.stripe.com/clover/stripe.js";
                            } else if (!s.stripe && typeof Stripe !== "undefined")
                                s.stripe = Stripe(this.dataset.stripePublishableKey);
                            return { $template: "checkout" };
                        case "/create-account":
                            return { $template: "create-account" };
                        case "/find-order":
                            return { $template: "find-order" };
                        case "/login":
                            return { $template: "login" };
                        case "/logout":
                            return {
                                $template: "logout",
                                noOp: !u0
                            };
                        case "/order-confirmation":
                            return {
                                $template: "order-confirmation",
                                stripePaymentIntentId: spp.get("payment_intent")
                            };
                    }
                    const m2 = p.match(productRegex);
                    if (m2)
                        return {
                            $template: "product",
                            slug: m2[1].substring(1),
                            search: location.search
                        };
                    const m3 = p.match(ordersRegex);
                    if (m3)
                        return m3[1] ? {
                            $template: "order",
                            id: m3[1].substring(1)
                        } : { $template: "orders" };
                    return p === "/shop" ? (() => {
                        return {
                            $template: "shop",
                            query: spp.get("q"),
                            category: spp.get("category"),
                            sort: spp.get("sort")
                        };
                    })() : {
                        $template: "page",
                        slug: (() => {
                            const s2 = p.substring(1);
                            return s2 ? s2 : "home";
                        })()
                    };
                })(),
                footer: {
                    $template: "footer",
                    navItems: s.footer?.navItems?.map(x => ({
                        $template: "link",
                        ...x,
                        document: x.type.name === "REFERENCE" ? `${x.document.$type}:${x.document.slug}` : null,
                        href: x.type.name === "CUSTOM" ? x.uri : null,
                        target: x.newTab ? "_blank" : null
                    })),
                    options: ["auto", "light", "dark"].map(x => ({
                        $template: "option",
                        value: x,
                        text: x.charAt(0).toUpperCase() + x.substring(1),
                        selected: x === (cs ?? "auto")
                    }))
                }
            }
        }));
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
                // if (!u.pathname.match(adminRegex) !== !location.pathname.match(adminRegex))
                // return;
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

    navigate(url) {
        delete this.serverState;
        delete this.state.notFound;
        if (url.pathname !== location.pathname)
            window.scrollTo(0, 0);
        history.pushState({}, "", url.pathname + url.search);
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

    success() {
        const el = this.querySelector("toaster-element");
        el.success.apply(el, arguments);
    }

    error() {
        const el = this.querySelector("toaster-element");
        el.error.apply(el, arguments);
    }
}
