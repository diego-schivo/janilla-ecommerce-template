package com.janilla.ecommercetemplate.backend;

import com.janilla.persistence.CrudObserver;
import com.janilla.persistence.Persistence;

public class ProductCrudObserver implements CrudObserver<Product> {

	protected final Persistence persistence;

	public ProductCrudObserver(Persistence persistence) {
		this.persistence = persistence;
	}

	@Override
	public Product afterRead(Product entity) {
		var xx = persistence.crud(Variant.class).filter("product", entity.id());
		return entity.withVariants(xx);
	}

	@Override
	public Product beforeCreate(Product entity) {
		if (entity.variants() != null)
			entity = entity.withVariants(null);
		return entity;
	}

	@Override
	public Product beforeUpdate(Product entity) {
		return beforeCreate(entity);
	}

	@Override
	public void afterUpdate(Product entity1, Product entity2) {
		var xx = persistence.crud(Variant.class).filter("product", entity1.id());
		if (entity2.variants() != null)
			xx = xx.stream().filter(x -> !entity2.variants().contains(x)).toList();
		persistence.crud(Variant.class).delete(xx);
	}

	@Override
	public void afterDelete(Product entity) {
		var xx = persistence.crud(Variant.class).filter("product", entity.id());
		persistence.crud(Variant.class).delete(xx);
	}
}
