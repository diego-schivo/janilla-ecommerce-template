/*
 * MIT License
 *
 * Copyright (c) 2018-2025 Payload CMS, Inc. <info@payloadcms.com>
 * Copyright (c) 2024-2026 Diego Schivo <diego.schivo@janilla.com>
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

export default class Payment extends WebComponent {

    static get templateNames() {
        return ["payment"];
    }

    static get observedAttributes() {
        return [];
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

        const s = this.state;
        if (!s.elements) {
            s.elements = a.state.stripe.elements({
                appearance: {
                    theme: "stripe",
                },
                clientSecret: this.closest("checkout-element").state.paymentData.clientSecret
                //loader: "auto"
            });
            s.elements.create("payment", { layout: "accordion" }).mount("#payment-element");
        }
    }

    handleSubmit = async event => {
        event.preventDefault();
        const c = this.closest("checkout-element");
		const a = this.closest("app-element");
		const [ba] = [c.state.billingAddress].map(x => typeof x === "object" ? x : a.state.user.addresses.find(y => y.id === x));
        let j = await a.state.stripe.confirmPayment({
            confirmParams: {
                return_url: `${location.origin}/order-confirmation`,
                payment_method_data: {
                    billing_details: {
                        email: this.dataset.email,
                        phone: ba.phone,
                        address: {
                            line1: ba.addressLine1,
                            line2: ba.addressLine2,
                            city: ba.city,
                            state: ba.state,
                            postal_code: ba.postalCode,
                            country: ba.country,
                        },
                    },
                }
            },
            elements: s.elements,
            redirect: "if_required"
        });
        if (j.paymentIntent?.status === "succeeded") {
            j = await (await fetch(`${a.dataset.apiUrl}/payments/stripe/confirm-order`, {
                method: "POST",
                headers: { "content-type": "application/json" },
                body: JSON.stringify({
                    guestEmail: this.dataset.guestEmail,
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
