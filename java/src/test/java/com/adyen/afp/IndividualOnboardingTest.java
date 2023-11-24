package com.adyen.afp;

import com.adyen.model.legalentitymanagement.LegalEntity;
import com.adyen.service.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IndividualOnboardingTest {

    @Test
    void run() throws IOException, ApiException {

        LegalEntity legalEntity = new IndividualOnboarding().run();

        assertNotNull(legalEntity);
    }
}
