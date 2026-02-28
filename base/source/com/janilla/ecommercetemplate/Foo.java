package com.janilla.ecommercetemplate;

import com.janilla.cms.User;
import com.janilla.java.Reflection;

public class Foo {

	public static void main(String[] args) {
		var p = Reflection.property(User.class, "id");
		IO.println(p);
	}
}
