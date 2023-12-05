package com.adyen.afp;

import com.adyen.model.transfers.Transaction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TransactionsTest {

    @Test
    void run() throws Exception {
        List<Transaction> transactions = new Transactions().run();
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());
    }
}
