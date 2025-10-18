package com.example.backend.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayHereConfig {

    @Value("${payhere.merchantId}")
    private String merchantId;

    @Value("${payhere.merchantSecret}")
    private String merchantSecret;

    @Value("${payhere.returnUrl}")
    private String returnUrl;

    @Value("${payhere.cancelUrl}")
    private String cancelUrl;

    @Value("${payhere.notifyUrl}")
    private String notifyUrl;

    // Optional default customer info if frontend does not provide
    @Value("${payhere.defaultFirstName:Client}")
    private String defaultFirstName;

    @Value("${payhere.defaultLastName:User}")
    private String defaultLastName;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantSecret() {
        return merchantSecret;
    }

    public void setMerchantSecret(String merchantSecret) {
        this.merchantSecret = merchantSecret;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getDefaultFirstName() {
        return defaultFirstName;
    }

    public void setDefaultFirstName(String defaultFirstName) {
        this.defaultFirstName = defaultFirstName;
    }

    public String getDefaultLastName() {
        return defaultLastName;
    }

    public void setDefaultLastName(String defaultLastName) {
        this.defaultLastName = defaultLastName;
    }
}
