package com.janilla.ecommercetemplate.backend;

import com.janilla.persistence.CrudObserver;
import com.janilla.persistence.Persistence;

public class VariantTypeCrudObserver implements CrudObserver<VariantType> {

	protected final Persistence persistence;

	public VariantTypeCrudObserver(Persistence persistence) {
		this.persistence = persistence;
	}

	@Override
	public VariantType afterRead(VariantType entity) {
		var xx = persistence.crud(VariantOption.class).filter("type", entity.id());
		return entity.withOptions(xx);
	}

	@Override
	public VariantType beforeCreate(VariantType entity) {
		if (entity.options() != null)
			entity = entity.withOptions(null);
		return entity;
	}

	@Override
	public VariantType beforeUpdate(VariantType entity) {
		return beforeCreate(entity);
	}

	@Override
	public void afterUpdate(VariantType entity1, VariantType entity2) {
		var xx = persistence.crud(VariantOption.class).filter("type", entity1.id());
		if (entity2.options() != null)
			xx = xx.stream().filter(x -> !entity2.options().contains(x)).toList();
		persistence.crud(VariantOption.class).delete(xx);
	}

	@Override
	public void afterDelete(VariantType entity) {
		var xx = persistence.crud(VariantOption.class).filter("type", entity.id());
		persistence.crud(VariantOption.class).delete(xx);
	}
}
