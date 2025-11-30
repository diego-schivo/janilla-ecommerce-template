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
import Account from "./account.js";
import AddressEdit from "./address-edit.js";
import Addresses from "./addresses.js";
import AdminArray from "./admin-array.js";
import AdminBar from "./admin-bar.js";
import AdminCheckbox from "./admin-checkbox.js";
import AdminCreateFirstUser from "./admin-create-first-user.js";
import AdminDashboard from "./admin-dashboard.js";
import AdminDocument from "./admin-document.js";
import AdminDrawer from "./admin-drawer.js";
import AdminDrawerLink from "./admin-drawer-link.js";
import AdminEdit from "./admin-edit.js";
import AdminHidden from "./admin-hidden.js";
import AdminJoin from "./admin-join.js";
import AdminList from "./admin-list.js";
import AdminLogin from "./admin-login.js";
import AdminRadioGroup from "./admin-radio-group.js";
import AdminRelationship from "./admin-relationship.js";
import AdminRichText from "./admin-rich-text.js";
import AdminSelect from "./admin-select.js";
import AdminSlug from "./admin-slug.js";
import AdminTabs from "./admin-tabs.js";
import AdminText from "./admin-text.js";
import AdminToasts from "./admin-toasts.js";
import AdminUnauthorized from "./admin-unauthorized.js";
import AdminUpload from "./admin-upload.js";
import AdminVariantOptions from "./admin-variant-options.js";
import App from "./app.js";
import Banner from "./banner.js";
import CallToAction from "./call-to-action.js";
import Card from "./card.js";
import Cart from "./cart.js";
import Checkout from "./checkout.js";
import Content from "./content.js";
import CustomAdmin from "./custom-admin.js";
import CustomAdminFields from "./custom-admin-fields.js";
import Hero from "./hero.js";
import IntlFormat from "./intl-format.js";
import Link from "./link.js";
import Login from "./login.js";
import Logout from "./logout.js";
import LucideIcon from "./lucide-icon.js";
import MediaBlock from "./media-block.js";
import MobileMenu from "./mobile-menu.js";
import NotFound from "./not-found.js";
import Order from "./order.js";
import OrderConfirmation from "./order-confirmation.js";
import Orders from "./orders.js";
import Page from "./page.js";
import Payment from "./payment.js";
import Price from "./price.js";
import Product from "./product.js";
import ProductDescription from "./product-description.js";
import RichText from "./rich-text.js";
import Select from "./select.js";
import Shop from "./shop.js";
import VariantSelector from "./variant-selector.js";

customElements.define("account-element", Account);
customElements.define("address-edit", AddressEdit);
customElements.define("addresses-element", Addresses);
customElements.define("admin-array", AdminArray);
customElements.define("admin-bar", AdminBar);
customElements.define("admin-checkbox", AdminCheckbox);
customElements.define("admin-create-first-user", AdminCreateFirstUser);
customElements.define("admin-dashboard", AdminDashboard);
customElements.define("admin-document", AdminDocument);
customElements.define("admin-drawer", AdminDrawer);
customElements.define("admin-drawer-link", AdminDrawerLink);
customElements.define("admin-edit", AdminEdit);
customElements.define("admin-element", CustomAdmin);
customElements.define("admin-fields", CustomAdminFields);
customElements.define("admin-hidden", AdminHidden);
customElements.define("admin-join", AdminJoin);
customElements.define("admin-list", AdminList);
customElements.define("admin-login", AdminLogin);
customElements.define("admin-radio-group", AdminRadioGroup);
customElements.define("admin-relationship", AdminRelationship);
customElements.define("admin-rich-text", AdminRichText);
customElements.define("admin-select", AdminSelect);
customElements.define("admin-slug", AdminSlug);
customElements.define("admin-tabs", AdminTabs);
customElements.define("admin-text", AdminText);
customElements.define("admin-toasts", AdminToasts);
customElements.define("admin-unauthorized", AdminUnauthorized);
customElements.define("admin-upload", AdminUpload);
customElements.define("admin-variant-options", AdminVariantOptions);
customElements.define("app-element", App);
customElements.define("banner-element", Banner);
customElements.define("call-to-action", CallToAction);
customElements.define("card-element", Card);
customElements.define("cart-element", Cart);
customElements.define("checkout-element", Checkout);
customElements.define("content-element", Content);
customElements.define("hero-element", Hero);
customElements.define("intl-format", IntlFormat);
customElements.define("link-element", Link);
customElements.define("login-element", Login);
customElements.define("logout-element", Logout);
customElements.define("lucide-icon", LucideIcon);
customElements.define("media-block", MediaBlock);
customElements.define("mobile-menu", MobileMenu);
customElements.define("not-found", NotFound);
customElements.define("order-element", Order);
customElements.define("order-confirmation", OrderConfirmation);
customElements.define("orders-element", Orders);
customElements.define("page-element", Page);
customElements.define("payment-element", Payment);
customElements.define("price-element", Price);
customElements.define("product-description", ProductDescription);
customElements.define("product-element", Product);
customElements.define("rich-text", RichText);
customElements.define("select-element", Select);
customElements.define("shop-element", Shop);
customElements.define("variant-selector", VariantSelector);
