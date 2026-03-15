package com.janilla.ecommercetemplate;

import com.janilla.java.JavaReflect;
import com.janilla.persistence.Entity;

public class Foo {

	public static void main(String[] args) {
		JavaReflect.actualTypeArguments(EcommerceUser.class, Entity.class);
	}
}
