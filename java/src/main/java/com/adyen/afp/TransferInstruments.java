package com.adyen.afp;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.legalentitymanagement.*;
import com.adyen.service.legalentitymanagement.TransferInstrumentsApi;

public class TransferInstruments {

    private static Client lemApiClient = null;
    private TransferInstrumentsApi transferInstrumentsApi;

    public TransferInstruments() {
        transferInstrumentsApi = new TransferInstrumentsApi(getLEMApiClient());
    }

    // Create, update, get payment instruments
    public TransferInstrument run() throws Exception {

        // note: cannot create multiple paymentInstruments with the same data
        // update accounts, ibans, etc.. to re-run
        final String ACCOUNT_NUMBER = "0000000123";
        final String ROUTING_NUMBER = "121202211";
        final String IBAN = "IT60X0542811101000000123456"; // when Legal Entity is in Italy

        // create EU bank account
        String ibanTransferInstrumentId = createIBANBankAccount(IBAN);

        // create US local bank account
        String usBankAccountTransferInstrumentId = createUsBankAccount(ACCOUNT_NUMBER, ROUTING_NUMBER);

        // update US local bank account
        TransferInstrument transferInstrument = updateBankAccount(usBankAccountTransferInstrumentId);

        // get US local bank account
        transferInstrument = getBankAccount(transferInstrument.getId());

        return transferInstrument;
    }
    String createUsBankAccount(String accountNumber, String routingNumber) throws Exception {

        TransferInstrumentInfo transferInstrumentInfo = new TransferInstrumentInfo()
                .legalEntityId(getLegalEntityId())
                .type(TransferInstrumentInfo.TypeEnum.BANKACCOUNT)
                .bankAccount(new BankAccountInfo()
                        .accountIdentification(new BankAccountInfoAccountIdentification(
                                new USLocalAccountIdentification()
                                        .type(USLocalAccountIdentification.TypeEnum.USLOCAL)
                                        .accountNumber(accountNumber)
                                        .routingNumber(routingNumber))
                        ));

        TransferInstrument transferInstrument = transferInstrumentsApi.createTransferInstrument(transferInstrumentInfo);

        return transferInstrument.getId();
    }

    String createIBANBankAccount(String iban) throws Exception {

        TransferInstrumentInfo transferInstrumentInfo = new TransferInstrumentInfo()
                .legalEntityId(getLegalEntityId())
                .type(TransferInstrumentInfo.TypeEnum.BANKACCOUNT)
                .bankAccount(new BankAccountInfo()
                        .accountIdentification(new BankAccountInfoAccountIdentification(
                                new IbanAccountIdentification()
                                        .type(IbanAccountIdentification.TypeEnum.IBAN)
                                        .iban(iban))
                        ));

        TransferInstrument transferInstrument = transferInstrumentsApi.createTransferInstrument(transferInstrumentInfo);

        return transferInstrument.getId();
    }

    TransferInstrument getBankAccount(String transferInstrumentId) throws Exception {
        TransferInstrument transferInstrument = transferInstrumentsApi.getTransferInstrument(transferInstrumentId);

        return transferInstrument;
    }

    TransferInstrument updateBankAccount(String transferInstrumentId) throws Exception {
        TransferInstrumentInfo transferInstrumentInfo = new TransferInstrumentInfo();
        transferInstrumentInfo.bankAccount(new BankAccountInfo()
                .bankName("updated bank name"));
        TransferInstrument updateTransferInstrument = transferInstrumentsApi.updateTransferInstrument(transferInstrumentId, transferInstrumentInfo);

        return updateTransferInstrument;
    }


    // create client to access the Legal Entity Management API
    private Client getLEMApiClient() {
        if (lemApiClient == null) {
            // create once
            lemApiClient = new Client(
                    getLEMApiKey(),
                    Environment.TEST); // change to LIVE on prod
        }

        return lemApiClient;
    }

    // LEM API key
    private String getLEMApiKey() {
        String key = System.getenv("LEM_API_KEY");

        if(key == null) {
            throw new RuntimeException("LEM_API_KEY is undefined");
        }

        return key;
    }

    // get an existing Legal Entity Id
    String getLegalEntityId() {
        String id = System.getenv("LEGAL_ENTITY_ID");

        if(id == null) {
            throw new RuntimeException("LEGAL_ENTITY_ID is undefined");
        }

        return id;
    }
}
