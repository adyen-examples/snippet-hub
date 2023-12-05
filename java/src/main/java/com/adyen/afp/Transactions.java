package com.adyen.afp;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.transfers.Transaction;
import com.adyen.model.transfers.TransactionSearchResponse;
import com.adyen.service.transfers.TransactionsApi;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transactions {

    private static Client bclApiClient = null;

    private TransactionsApi transactionsApi;

    public Transactions() {
        transactionsApi = new TransactionsApi(getBCLApiClient());
    }

    public List<Transaction> run() throws Exception {

        List<Transaction> transactions = getPlatformTransactions();

        List<Transaction> paginatedTransactions = getPaginatedPlatformTransactions();

        return transactions;
    }

    // get transactions for given platform
    public List<Transaction> getPlatformTransactions() throws Exception {

        // in the last X days
        OffsetDateTime createdSince = OffsetDateTime.now().minus(365, ChronoUnit.DAYS);
        // until today
        OffsetDateTime createdUntil = OffsetDateTime.now();
        // max number of transactions to fetch
        Integer limit = 10;

        TransactionSearchResponse transactionSearchResponse = transactionsApi.getAllTransactions(
                getBalancePlatform(), null, null, null,
                null, createdSince, createdUntil, limit, null);

        return transactionSearchResponse.getData();
    }

    // get transactions and follow pagination access
    public List<Transaction> getPaginatedPlatformTransactions() throws Exception {

        // in the last X days
        OffsetDateTime createdSince = OffsetDateTime.now().minus(365, ChronoUnit.DAYS);
        // until today
        OffsetDateTime createdUntil = OffsetDateTime.now();
        // max number of transactions to fetch (per page)
        Integer limit = 2;

        // first result
        TransactionSearchResponse transactionSearchResponse = transactionsApi.getAllTransactions(
                getBalancePlatform(), null, null, null,
                null, createdSince, createdUntil, limit, null);

        // get next
        if(transactionSearchResponse.getLinks() != null && transactionSearchResponse.getLinks().getNext() != null) {
            String nextHref = transactionSearchResponse.getLinks().getNext().getHref();
            String cursor = extractCursor(nextHref);

            transactionSearchResponse = transactionsApi.getAllTransactions(
                    getBalancePlatform(), null, null, null,
                    cursor, createdSince, createdUntil, limit, null);
        }

        // get previous
        if(transactionSearchResponse.getLinks() != null && transactionSearchResponse.getLinks().getPrev() != null) {
            String prevHref = transactionSearchResponse.getLinks().getPrev().getHref();
            String cursor = extractCursor(prevHref);

            transactionSearchResponse = transactionsApi.getAllTransactions(
                    getBalancePlatform(), null, null, null,
                    cursor, createdSince, createdUntil, limit, null);
        }

        return transactionSearchResponse.getData();
    }

    // obtain pagination curson from given href
    private static String extractCursor(String nextHref) {
        String cursorRegex = "[?&]cursor=([^&]+)";
        Pattern pattern = Pattern.compile(cursorRegex);
        Matcher matcher = pattern.matcher(nextHref);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    // create client to access the Configuration API
    private Client getBCLApiClient() {
        if (bclApiClient == null) {
            // create once
            bclApiClient = new Client(
                    getBCLApiKey(),
                    Environment.TEST); // change to LIVE on prod
        }

        return bclApiClient;
    }

    // Configuration API key
    private String getBCLApiKey() {
        String key = System.getenv("BCL_API_KEY");

        if(key == null) {
            throw new RuntimeException("BCL_API_KEY is undefined");
        }

        return key;
    }

    private String getBalancePlatform() {
        String id = System.getenv("BALANCE_PLATFORM");

        if(id == null) {
            throw new RuntimeException("BALANCE_PLATFORM is undefined");
        }

        return id;
    }
}
