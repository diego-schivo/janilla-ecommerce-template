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
package com.janilla.ecommercetemplate.backend;

import com.janilla.backend.persistence.CrudObserver;
import com.janilla.backend.persistence.Persistence;

public class UserCrudObserver implements CrudObserver<UserImpl> {

	protected final Persistence persistence;

	public UserCrudObserver(Persistence persistence) {
		this.persistence = persistence;
	}

	@Override
	public UserImpl afterRead(UserImpl entity) {
		var e = entity;
		var cc = persistence.crud(Cart.class).filter("customer", new Object[] { e.id() });
		e = e.withCarts(cc);
		var aa = persistence.crud(Address.class).filter("customer", new Object[] { e.id() });
		e = e.withAddresses(aa);
		return e;
	}

	@Override
	public UserImpl beforeCreate(UserImpl entity) {
		var e = entity;
		if (e.carts() != null)
			e = e.withCarts(null);
		if (e.addresses() != null)
			e = e.withAddresses(null);
		return e;
	}

	@Override
	public UserImpl beforeUpdate(UserImpl entity) {
		return beforeCreate(entity);
	}

//	@Override
//	public void afterUpdate(User entity1, User entity2) {
//		var cc = persistence.crud(Cart.class).filter("customer", entity1.id());
//		if (entity2.carts() != null)
//			cc = cc.stream().filter(x -> !entity2.carts().contains(x)).toList();
//		persistence.crud(Cart.class).delete(cc);
//		
//		var aa = persistence.crud(Address.class).filter("customer", entity1.id());
//		if (entity2.addresses() != null)
//			aa = aa.stream().filter(x -> !entity2.addresses().contains(x)).toList();
//		persistence.crud(Address.class).delete(aa);
//	}

	@Override
	public void afterDelete(UserImpl entity) {
		var cc = persistence.crud(Cart.class).filter("customer", new Object[] { entity.id() });
		persistence.crud(Cart.class).delete(cc);
		var aa = persistence.crud(Address.class).filter("customer", new Object[] { entity.id() });
		persistence.crud(Address.class).delete(aa);
	}
}
