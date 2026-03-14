package com.janilla.ecommercetemplate;

import java.util.List;

import com.janilla.cms.User;

public interface EcommerceUser<ID extends Comparable<ID>> extends User<ID> {

	List<Cart> carts();

	List<Address> addresses();

	EcommerceUser<ID> withCarts(List<Cart> carts);

	EcommerceUser<ID> withAddresses(List<Address> addresses);
}
