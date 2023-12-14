package com.adyen.afp;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.legalentitymanagement.*;
import com.adyen.service.legalentitymanagement.BusinessLinesApi;
import com.adyen.service.legalentitymanagement.LegalEntitiesApi;

import java.util.List;

public class BusinessLines {

    private static Client lemApiClient = null;
    private BusinessLinesApi businessLinesApi;
    private LegalEntitiesApi legalEntitiesApi = null;


    public BusinessLines() {
        this.businessLinesApi = new BusinessLinesApi(getLEMApiClient());
        this.legalEntitiesApi = new LegalEntitiesApi(getLEMApiClient());
    }

    public BusinessLine run() throws Exception {

        // create Business Line
        BusinessLine businessLine = createBusinessLine();
        // get Business Line
        businessLine = businessLinesApi.getBusinessLine(businessLine.getId());
        // get all Business Lines for the Legal Entity
        List<BusinessLine> businessLines = getBusinessLines();
        // find newly created Business Line
        for(BusinessLine b : businessLines) {
            if(b.getId().equals(businessLine.getId())) {
                // update
                businessLinesApi.updateBusinessLine(b.getId(), new BusinessLineInfoUpdate()
                        .addWebDataItem(new WebData().webAddress("https://www.adyen.com")));
                // delete
                businessLinesApi.deleteBusinessLine(b.getId());
            }
        }

        return businessLine;
    }

    BusinessLine createBusinessLine() throws Exception {

        var businessLineInfo = new BusinessLineInfo()
                .legalEntityId(getLegalEntityId())
                .service(BusinessLineInfo.ServiceEnum.PAYMENTPROCESSING)
                .industryCode("4531")
                .webData(List.of(new WebData().webAddress("https://example.com/")))
                .sourceOfFunds(new SourceOfFunds()
                        .type(SourceOfFunds.TypeEnum.BUSINESS)
                        .adyenProcessedFunds(false)
                        .description("my Business Line"));

        return businessLinesApi.createBusinessLine(businessLineInfo);
    }

    List<BusinessLine> getBusinessLines() throws Exception {

        com.adyen.model.legalentitymanagement.BusinessLines businessLines = legalEntitiesApi.getAllBusinessLinesUnderLegalEntity(getLegalEntityId());

        return businessLines.getBusinessLines();



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

        if (key == null) {
            throw new RuntimeException("LEM_API_KEY is undefined");
        }

        return key;
    }

    // get an existing Legal Entity Id
    String getLegalEntityId() {
        String id = System.getenv("LEGAL_ENTITY_ID");

        if (id == null) {
            throw new RuntimeException("LEGAL_ENTITY_ID is undefined");
        }

        return id;
    }
}
