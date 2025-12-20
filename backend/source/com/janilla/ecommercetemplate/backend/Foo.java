package com.janilla.ecommercetemplate.backend;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import com.janilla.java.Converter;
import com.janilla.json.Json;
import com.janilla.reflect.Reflection;

public class Foo {

	public static void main(String[] args) {
		var mm = Arrays.stream(UserApi.class.getMethods()).filter(x -> x.getName().equals("create")).toList();
		var t = Reflection.actualParameterTypes(mm.get(0), UserApi.class)[0];
		IO.println("t=" + t);
		var t2 = ((ParameterizedType) t).getActualTypeArguments()[0];
		IO.println("t2=" + t2);

//		var rc = Java.toClass(t).getRecordComponents()[0];
//		IO.println("rc=" + rc + " " + rc.getGenericType());
//
//		var m = Reflection.actualTypeArguments(t, rc.getDeclaringRecord());
//		IO.println("m=" + m);
//
//		var rct = Reflection.actualType(rc, t);
//		IO.println("rct=" + rct);

		var s = """
				{
				    "email": "foo5@example.com",
				    "password": "password",
				    "passwordConfirm": "password"
				}""";
		var m = Json.parse(s);
		var o = new Converter().convert(m, t);
		IO.println("o=" + o);

//		IO.println(Reflection.propertyNames(t).toList());

//		for (var c : Java.toClass(t).getRecordComponents()) {
//			IO.println("c=" + c.getGenericType());
//		}

//		class C1<T> {
//		}
//		class C2 extends C1<String> {
//		}
//		var m = Reflection.actualTypeArguments(C2.class, C1.class);
//		IO.println("m=" + m);
	}
}
