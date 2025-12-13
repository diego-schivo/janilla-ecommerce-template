package com.janilla.ecommercetemplate.backend;

import com.janilla.cms.Types;

public record GalleryItem(@Types(Media.class) Long image, @Types(VariantOption.class) Long variantOption) {
}