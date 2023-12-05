package com.adyen.checkout;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class AdvancedFlow {

    private static Client checkoutApiClient = null;
    private PaymentsApi paymentsApi;

    public AdvancedFlow() {
        paymentsApi = new PaymentsApi(getCheckoutClient());
    }

    public void run() throws Exception {

        var paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(getMerchantAccount());
        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);

        var paymentMethodsResponse = paymentsApi.paymentMethods(paymentMethodsRequest);

        PaymentRequest paymentRequest = new PaymentRequest();

        var orderRef = UUID.randomUUID().toString();

        // pay with SEPA
        CheckoutPaymentMethod checkoutPaymentMethod = new CheckoutPaymentMethod(
                new SepaDirectDebitDetails()
                        .ownerName("J.Smith")
                        .iban("NL13TEST0123456789"));

        paymentRequest
                .paymentMethod(checkoutPaymentMethod)
                .amount(new Amount()
                        .currency("EUR")
                        .value(10000L))
                .merchantAccount(getMerchantAccount())
                .channel(PaymentRequest.ChannelEnum.WEB)
                .reference(orderRef)
                .returnUrl("https://example.com/api/handleShopperRedirect?orderRef=" + orderRef);

        var paymentResponse = paymentsApi.payments(paymentRequest);
    }

    private Client getCheckoutClient() {
        if (checkoutApiClient == null) {
            // create once
            checkoutApiClient = new Client(
                    getCheckoutApiKey(),
                    Environment.TEST); // change to LIVE on prod
        }

        return checkoutApiClient;
    }

    // Checkout API key
    private String getCheckoutApiKey() {
        String key = System.getenv("CHECKOUT_API_KEY");

        if(key == null) {
            throw new RuntimeException("CHECKOUT_API_KEY is undefined");
        }

        return key;
    }

    private String getMerchantAccount() {
        String id = System.getenv("MERCHANT_ACCOUNT");

        if(id == null) {
            throw new RuntimeException("MERCHANT_ACCOUNT is undefined");
        }

        return id;
    }
}
