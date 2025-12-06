package com.janilla.ecommercetemplate.backend;

import com.janilla.cms.Types;

public record CartItem(@Types(Product.class) Long product, @Types(Variant.class) Long variant, Long quantity) {
}
