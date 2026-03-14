package com.janilla.ecommercetemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.janilla.cms.User;
import com.janilla.websitetemplate.WebsiteConstants;

public class EcommerceConstants extends WebsiteConstants {

	public Stream<Country> countries() {
		return Arrays.stream(CountryImpl.class.getEnumConstants());
	}

	public Currency currency(String name) {
		return CurrencyImpl.valueOf(name);
	}

	public Address emptyAddress() {
		return AddressImpl.EMPTY;
	}

	public Cart emptyCart() {
		return CartImpl.EMPTY;
	}

	public Order newOrder(List<CartItem> items, AddressData shippingAddress, User<?> customer, String customerEmail,
			List<Transaction> transactions, OrderStatus status, BigDecimal amount, Currency currency) {
		return new OrderImpl(null, items, shippingAddress, customer, customerEmail, transactions, status, amount,
				currency, null, null, null, null);
	}

	public Transaction newTransaction(List<CartItem> items, PaymentMethod paymentMethod, AddressData billingAddress,
			TransactionStatus status, User<?> customer, String customerEmail, Order order, Cart cart, BigDecimal amount,
			Currency currency, String stripeCustomer, String stripePaymentIntent) {
		return new TransactionImpl(null, items, paymentMethod, billingAddress, status, customer, customerEmail, order,
				cart, amount, currency, stripeCustomer, stripePaymentIntent, null, null, null, null);
	}

	public TransactionStatus pendingTransactionStatus() {
		return TransactionStatusImpl.PENDING;
	}

	public OrderStatus processingOrderStatus() {
		return OrderStatusImpl.PROCESSING;
	}

	public PaymentMethod stripePaymentMethod() {
		return PaymentMethodImpl.STRIPE;
	}

	public TransactionStatus succeededTransactionStatus() {
		return TransactionStatusImpl.SUCCEEDED;
	}

	public Stream<Title> titles() {
		return Arrays.stream(TitleImpl.class.getEnumConstants());
	}
}
