package com.adyen.afp;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.legalentitymanagement.DataReviewConfirmationResponse;
import com.adyen.model.legalentitymanagement.VerificationErrors;
import com.adyen.service.legalentitymanagement.LegalEntitiesApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegalEntityManagement {

    private final static Logger LOGGER = LoggerFactory.getLogger(LegalEntityManagement.class);

    private static Client lemApiClient = null;

    private LegalEntitiesApi legalEntitiesApi = null;

    public LegalEntityManagement() {
        legalEntitiesApi = new LegalEntitiesApi(getLEMApiClient());
    }

    public VerificationErrors checkVerificationErrors(String legalEntityId) throws Exception {

        VerificationErrors verificationErrors = legalEntitiesApi.checkLegalEntitysVerificationErrors(getLegalEntityId());

        for(var problem : verificationErrors.getProblems()) {
            LOGGER.info("Error related to capability: {}", problem.getEntity());
            for(var verificationError : problem.getVerificationErrors()) {
                var type = verificationError.getType(); // error type
                var errorCode = verificationError.getCode();  // error code
                var message = verificationError.getMessage(); // description of the error
                var remediatingActions = verificationError.getRemediatingActions(); // what can be done

                LOGGER.info("Verification error type: {} message: {}", type, message);
            }
        }

        return verificationErrors;
    }

    public String confirmDataReview(String legalEntityId) throws Exception {

        DataReviewConfirmationResponse response = legalEntitiesApi.confirmDataReview(legalEntityId);

        String ts = response.getDataReviewedAt();
        LOGGER.info("The data was reviewed by the user and confirmed by you on {}", ts);

        return ts;
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
