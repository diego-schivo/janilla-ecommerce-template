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

export default class Product extends WebComponent {

    static get templateNames() {
        return ["product"];
    }

    static get observedAttributes() {
        return ["data-slug", "data-search"];
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
        const a = this.closest("app-element");
        const s = this.state;
        s.product = a.serverState?.product;
        if (!s.product) {
            const u = new URL(`${a.dataset.apiUrl}/products`, location.href);
            u.searchParams.append("slug", this.dataset.slug);
            s.product = (await (await fetch(u)).json())[0];
        }
        const pp = new URLSearchParams(this.dataset.search);
        this.appendChild(this.interpolateDom({
            $template: "",
            ...(s.product.enableVariants ? {
                variantOptions: s.product.variantTypes.map(x => pp.get(x.name)).filter(x => x),
                variant: pp.get("variant")
            } : {}),
            layout: s.product.layout?.map((x, i) => ({
                $template: x.$type.split(/(?=[A-Z])/).map(x => x.toLowerCase()).join("-"),
                path: `layout.${i}`
            }))
        }));
    }

    handleClick = async event => {
        const b = event.target.closest("button");
        const ul1 = b?.closest("#gallery-arrows ul");
        const s = this.state;
        if (ul1) {
            let i = s.galleryIndex;
            i += [-1, 1][Array.prototype.findIndex.call(ul1.children, x => x.contains(b))];
            s.galleryIndex = (s.product.gallery.length + i) % s.product.gallery.length;
            this.requestDisplay();
        }
        const ul2 = b?.closest("#gallery-thumbnails ul");
        if (ul2) {
            s.galleryIndex = Array.prototype.findIndex.call(ul2.children, x => x.contains(b));
            this.requestDisplay();
        }
    }

    data(path) {
        return path.split(".").reduce((x, n) => Array.isArray(x)
            ? x[parseInt(n)]
            : typeof x === "object" && x !== null
                ? x[n]
                : null, this.state.product);
    }
}
