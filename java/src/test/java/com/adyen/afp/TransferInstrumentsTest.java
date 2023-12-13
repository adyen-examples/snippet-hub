package com.adyen.afp;

import com.adyen.model.legalentitymanagement.TransferInstrument;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferInstrumentsTest {

    @Test
    void run() throws Exception {
        TransferInstrument transferInstrument = new TransferInstruments().run();
        assertNotNull(transferInstrument);
    }
}