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
package com.janilla.ecommercetemplate.backend;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import com.janilla.cms.CmsPersistence;
import com.janilla.ioc.DiFactory;
import com.janilla.java.Converter;
import com.janilla.java.Java;
import com.janilla.java.TypeResolver;
import com.janilla.json.Json;
import com.janilla.persistence.Crud;
import com.janilla.persistence.CrudObserver;
import com.janilla.persistence.Entity;
import com.janilla.reflect.Reflection;
import com.janilla.sqlite.SqliteDatabase;

public class CustomPersistence extends CmsPersistence {

//	protected static final CrudObserver USER_OBSERVER = new CrudObserver() {
//
//		@Override
//		public <E> E beforeCreate(E entity) {
//			var u = (User) entity;
//			var c = u.cart();
//			if (c != null)
//				c = c.withNonNullItemIds();
//			@SuppressWarnings("unchecked")
//			var e = (E) (c != u.cart() ? u.withCart(c) : u);
//			return e;
//		}
//
//		@Override
//		public <E> E beforeUpdate(E entity) {
//			return beforeCreate(entity);
//		}
//	};

	protected final DiFactory diFactory;

	protected final Properties configuration;

	public CustomPersistence(SqliteDatabase database, Collection<Class<? extends Entity<?>>> types,
			TypeResolver typeResolver, DiFactory diFactory, Properties configuration) {
		this.diFactory = diFactory;
		this.configuration = configuration;
		super(database, types, typeResolver);
	}

	@Override
	protected <E extends Entity<?>> Crud<?, E> newCrud(Class<E> type) {
//		if (type == User.class)
//			return (Crud<?, E>) new UserCrud(this);
		var x = super.newCrud(type);
		if (x != null) {
			if (type == Product.class)
				x.observers()
						.add((CrudObserver) diFactory.create(ProductCrudObserver.class, Map.of("persistence", this)));
			else if (type == User.class)
				x.observers().add((CrudObserver) diFactory.create(UserCrudObserver.class, Map.of("persistence", this)));
			else if (type == VariantType.class)
				x.observers().add(
						(CrudObserver) diFactory.create(VariantTypeCrudObserver.class, Map.of("persistence", this)));
		}
		return x;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void seed() throws IOException {
		Reflection.properties(SeedData.class).forEach(x -> database.perform(() -> {
			var t = x.genericType() instanceof ParameterizedType pt ? (Class<?>) pt.getActualTypeArguments()[0]
					: x.type();
			var c = crud((Class) t);
			c.delete(c.list());
			return null;
		}, true));

		SeedData sd;
		try (var is = getClass().getResourceAsStream("seed-data.json")) {
			var s = new String(is.readAllBytes());
			var o = Json.parse(s);
			sd = diFactory.create(Converter.class).convert(o, SeedData.class);
		}

		Reflection.properties(SeedData.class).forEach(x -> database.perform(() -> {
			var t = x.genericType() instanceof ParameterizedType pt ? (Class<?>) pt.getActualTypeArguments()[0]
					: x.type();
			var c = crud((Class) t);
			var o = x.get(sd);
			(o instanceof List<?> oo ? oo.stream() : Stream.of(o)).forEach(y -> c.create((Entity) y));
			return null;
		}, true));

		var r = getClass().getResource("seed-data.zip");
		URI u;
		try {
			u = r.toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		if (!u.toString().startsWith("jar:"))
			u = URI.create("jar:" + u);
		var s = Java.zipFileSystem(u).getPath("/");
//		var d = Files.createDirectories(databaseFile.getParent().resolve("ecommerce-template-upload"));
		var ud = configuration.getProperty("ecommerce-template.upload.directory");
		if (ud.startsWith("~"))
			ud = System.getProperty("user.home") + ud.substring(1);
		var d = Files.createDirectories(Path.of(ud));
		Files.walkFileTree(s, new SimpleFileVisitor<>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				var t = d.resolve(s.relativize(file).toString());
				Files.copy(file, t, StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
