package com.janilla.ecommercetemplate.backend;

import com.janilla.persistence.CrudObserver;
import com.janilla.persistence.Persistence;

public class UserCrudObserver implements CrudObserver<User> {

	protected final Persistence persistence;

	public UserCrudObserver(Persistence persistence) {
		this.persistence = persistence;
	}

	@Override
	public User afterRead(User entity) {
		var e = entity;
		var cc = persistence.crud(Cart.class).filter("customer", e.id());
		e = e.withCarts(cc);
		var aa = persistence.crud(Address.class).filter("customer", e.id());
		e = e.withAddresses(aa);
		return e;
	}

	@Override
	public User beforeCreate(User entity) {
		var e = entity;
		if (e.carts() != null)
			e = e.withCarts(null);
		if (e.addresses() != null)
			e = e.withAddresses(null);
		return e;
	}

	@Override
	public User beforeUpdate(User entity) {
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
	public void afterDelete(User entity) {
		var cc = persistence.crud(Cart.class).filter("customer", entity.id());
		persistence.crud(Cart.class).delete(cc);
		var aa = persistence.crud(Address.class).filter("customer", entity.id());
		persistence.crud(Address.class).delete(aa);
	}
}
