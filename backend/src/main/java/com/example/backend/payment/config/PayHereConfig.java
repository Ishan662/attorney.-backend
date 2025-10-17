package com.example.backend.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

/**
 * Holds PayHere configuration values from application.properties.
 */
@Configuration
@Getter
public class PayHereConfig {

    @Value("${payhere.merchantId}")
    private String merchantId;

    @Value("${payhere.returnUrl}")
    private String returnUrl;

    @Value("${payhere.cancelUrl}")
    private String cancelUrl;

    @Value("${payhere.notifyUrl}")
    private String notifyUrl;
}
