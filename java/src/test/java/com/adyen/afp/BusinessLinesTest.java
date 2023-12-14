package com.adyen.afp;

import com.adyen.model.legalentitymanagement.BusinessLine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessLinesTest {

    @Test
    void run() throws Exception {
        BusinessLine businessLine = new BusinessLines().run();
        assertNotNull(businessLine);
    }
}