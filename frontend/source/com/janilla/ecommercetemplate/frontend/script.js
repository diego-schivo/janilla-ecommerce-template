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
import Account from "account";
import AccountNav from "account-nav";
import AddressEdit from "address-edit";
import AddressItem from "address-item";
import Addresses from "addresses";
import Admin from "admin";
import AdminArray from "admin-array";
import AdminBar from "admin-bar";
import AdminCheckbox from "admin-checkbox";
import AdminCreateFirstUser from "admin-create-first-user";
import AdminDashboard from "admin-dashboard";
import AdminDocument from "admin-document";
import AdminDrawer from "admin-drawer";
import AdminDrawerLink from "admin-drawer-link";
import AdminEdit from "admin-edit";
import AdminFields from "admin-fields";
import AdminFile from "admin-file";
import AdminHidden from "admin-hidden";
import AdminJoin from "admin-join";
import AdminList from "admin-list";
import AdminLogin from "admin-login";
import AdminRadioGroup from "admin-radio-group";
import AdminRelationship from "admin-relationship";
import AdminRichText from "admin-rich-text";
import AdminSelect from "admin-select";
import AdminSlug from "admin-slug";
import AdminTabs from "admin-tabs";
import AdminText from "admin-text";
import AdminUnauthorized from "admin-unauthorized";
import AdminUpload from "admin-upload";
import AdminVariantOptions from "admin-variant-options";
import AdminVersion from "admin-version";
import AdminVersions from "admin-versions";
import App from "app";
import CallToAction from "call-to-action";
import Card from "card";
import CartModal from "cart-modal";
import Checkout from "checkout";
import CheckoutAddresses from "checkout-addresses";
import ConfirmOrder from "confirm-order";
import Content from "content";
import CreateAccount from "create-account";
import CreateAddressModal from "create-address-modal";
import FindOrder from "find-order";
import Footer from "footer";
import Header from "header";
import Hero from "hero";
import IntlFormat from "intl-format";
import JanillaLogo from "janilla-logo";
import Link from "link";
import LoadingSpinner from "loading-spinner";
import Login from "login";
import Logout from "logout";
import LucideIcon from "lucide-icon";
import MediaBlock from "media-block";
import Message from "message";
import MobileMenu from "mobile-menu";
import NotFound from "not-found";
import Order from "order";
import OrderItem from "order-item";
import Orders from "orders";
import Page from "page";
import Payment from "payment";
import Price from "price";
import Product from "product";
import ProductDescription from "product-description";
import ProductGallery from "product-gallery";
import ProductItem from "product-item";
import Select from "select";
import Shop from "shop";
import Toaster from "toaster";
import VariantSelector from "variant-selector";

customElements.define("account-element", Account);
customElements.define("account-nav", AccountNav);
customElements.define("address-edit", AddressEdit);
customElements.define("address-item", AddressItem);
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
customElements.define("admin-element", Admin);
customElements.define("admin-fields", AdminFields);
customElements.define("admin-file", AdminFile);
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
customElements.define("admin-unauthorized", AdminUnauthorized);
customElements.define("admin-upload", AdminUpload);
customElements.define("admin-variant-options", AdminVariantOptions);
customElements.define("admin-version", AdminVersion);
customElements.define("admin-versions", AdminVersions);
customElements.define("app-element", App);
customElements.define("call-to-action", CallToAction);
customElements.define("card-element", Card);
customElements.define("cart-modal", CartModal);
customElements.define("checkout-addresses", CheckoutAddresses);
customElements.define("checkout-element", Checkout);
customElements.define("confirm-order", ConfirmOrder);
customElements.define("content-element", Content);
customElements.define("create-account", CreateAccount);
customElements.define("create-address-modal", CreateAddressModal);
customElements.define("find-order", FindOrder);
customElements.define("footer-element", Footer);
customElements.define("header-element", Header);
customElements.define("hero-element", Hero);
customElements.define("intl-format", IntlFormat);
customElements.define("janilla-logo", JanillaLogo);
customElements.define("link-element", Link);
customElements.define("loading-spinner", LoadingSpinner);
customElements.define("login-element", Login);
customElements.define("logout-element", Logout);
customElements.define("lucide-icon", LucideIcon);
customElements.define("media-block", MediaBlock);
customElements.define("message-element", Message);
customElements.define("mobile-menu", MobileMenu);
customElements.define("not-found", NotFound);
customElements.define("order-element", Order);
customElements.define("order-item", OrderItem);
customElements.define("orders-element", Orders);
customElements.define("page-element", Page);
customElements.define("payment-element", Payment);
customElements.define("price-element", Price);
customElements.define("product-description", ProductDescription);
customElements.define("product-element", Product);
customElements.define("product-gallery", ProductGallery);
customElements.define("product-item", ProductItem);
customElements.define("select-element", Select);
customElements.define("shop-element", Shop);
customElements.define("toaster-element", Toaster);
customElements.define("variant-selector", VariantSelector);
