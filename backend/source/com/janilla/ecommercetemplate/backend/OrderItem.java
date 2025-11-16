package com.janilla.ecommercetemplate.backend;

public record OrderItem(String id, Long product, String variant, Integer quantity) {
}
