package com.janilla.ecommercetemplate.frontend;

import java.util.Map;

import com.janilla.frontend.App;

record AppImpl(String key, String apiUrl, String stripePublishableKey, String stripeUrl, Map<String, Object> state) implements App {
}
