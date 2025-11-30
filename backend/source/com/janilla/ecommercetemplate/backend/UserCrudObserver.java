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
		var aa = persistence.crud(Address.class).filter("customer", entity.id());
		return entity.withAddresses(aa);
	}

	@Override
	public User beforeCreate(User entity) {
		var c = entity.cart();
		if (c != null)
			c = c.withNonNullItemIds();
		var e = c != entity.cart() ? entity.withCart(c) : entity;
		if (e.addresses() != null)
			e = e.withAddresses(null);
		return e;
	}

	@Override
	public User beforeUpdate(User entity) {
		return beforeCreate(entity);
	}

	@Override
	public void afterUpdate(User entity1, User entity2) {
		var aa = persistence.crud(Address.class).filter("customer", entity1.id());
		if (entity2.addresses() != null)
			aa = aa.stream().filter(x -> !entity2.addresses().contains(x)).toList();
		persistence.crud(Address.class).delete(aa);
	}

	@Override
	public void afterDelete(User entity) {
		var aa = persistence.crud(Address.class).filter("customer", entity.id());
		persistence.crud(Address.class).delete(aa);
	}
}
