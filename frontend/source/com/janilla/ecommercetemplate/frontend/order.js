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

export default class Order extends WebComponent {

	static get templateNames() {
		return ["order"];
	}

	static get observedAttributes() {
		return ["data-id"];
	}

	constructor() {
		super();
	}

	async updateDisplay() {
		const a = this.closest("app-element");
		const s = this.state;
		s.order ??= a.serverState?.order ?? await (await fetch(`${a.dataset.apiUrl}/orders/${this.dataset.id}`)).json();
		this.appendChild(this.interpolateDom({
			$template: "",
			...s.order,
			items: s.order.items.map(x => ({
				$template: "item",
				item: JSON.stringify(x)
			})),
			shippingAddress: JSON.stringify(s.order.shippingAddress)
		}));
	}
}
