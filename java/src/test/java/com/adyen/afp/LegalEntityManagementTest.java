package com.adyen.afp;

import com.adyen.model.legalentitymanagement.VerificationErrors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegalEntityManagementTest {

    LegalEntityManagement legalEntityManagement = new LegalEntityManagement();

    @Test
    void checkVerificationErrors() throws Exception {
        // get existing LegalEntityId from env variable
        String LEGAL_ENTITY_ID = legalEntityManagement.getLegalEntityId();

        VerificationErrors verificationErrors = legalEntityManagement.checkVerificationErrors(LEGAL_ENTITY_ID);
        assertNotNull(verificationErrors);
    }

    @Test
    void confirmDataReview() throws Exception {
        // get existing LegalEntityId from env variable
        String LEGAL_ENTITY_ID = legalEntityManagement.getLegalEntityId();

        String timestamp = legalEntityManagement.confirmDataReview(LEGAL_ENTITY_ID);
        assertNotNull(timestamp);
    }
}