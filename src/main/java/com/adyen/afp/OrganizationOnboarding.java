package com.adyen.afp;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.balanceplatform.AccountHolder;
import com.adyen.model.balanceplatform.AccountHolderInfo;
import com.adyen.model.legalentitymanagement.*;
import com.adyen.service.balanceplatform.AccountHoldersApi;
import com.adyen.service.exception.ApiException;
import com.adyen.service.legalentitymanagement.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class OrganizationOnboarding {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrganizationOnboarding.class);

    private static Client lemApiClient = null;
    private static Client bclApiClient = null;
    private LegalEntitiesApi legalEntitiesApi = null;
    private AccountHoldersApi accountHoldersApi = null;
    private DocumentsApi documentsApi;
    private TransferInstrumentsApi transferInstrumentsApi;
    private BusinessLinesApi businessLinesApi;
    private TermsOfServiceApi termsOfServiceApi;

    public OrganizationOnboarding() {
        legalEntitiesApi = new LegalEntitiesApi(getLEMApiClient());
        accountHoldersApi = new AccountHoldersApi(getBCLApiClient());
        documentsApi = new DocumentsApi(getLEMApiClient());
        transferInstrumentsApi = new TransferInstrumentsApi(getLEMApiClient());
        businessLinesApi = new BusinessLinesApi(getLEMApiClient());
        termsOfServiceApi = new TermsOfServiceApi(getLEMApiClient());
    }

    // Perform the steps involved in the onboarding of an organization
    public LegalEntity run() throws IOException, ApiException {

        // create Legal Entity for the Organization
        LegalEntity legalEntity = createLegalOrganization();

        // create Legal Entity CEO
        LegalEntity legalEntityCEO = createLegalEntityCEO();
        Document documentForLegalEntityCEO = createDocumentForLegalEntityCEO(legalEntityCEO.getId());

        // create Legal Entity Country Manager
        LegalEntity legalEntityCountryManager = createLegalEntityCountryManager();
        Document documentForLegalEntityCountryManager = createDocumentForLegalEntityCountryManager(legalEntityCEO.getId());

        // associate individuals to the Organization
        legalEntityCEO = associateCEO(legalEntity.getId(), legalEntityCEO.getId());
        legalEntityCountryManager = associateCountryManager(legalEntity.getId(), legalEntityCountryManager.getId());

        // create Account Holder
        AccountHolder accountHolder = createAccountHolder(legalEntity.getId());

        // create TransferInstrument
        TransferInstrument transferInstrument = createTransferInstrument(legalEntity.getId());
        Document documentForTransferInstrument = createDocumentForTransferInstrument(transferInstrument.getId());

        // create Business Line
        BusinessLine businessLine = createBusinessLine(legalEntity.getId());

        // Accept Adyen's Terms of Service (https://docs.adyen.com/marketplaces-and-platforms/collect-verification-details/custom/terms-of-service/)

        // determine the type of Terms of Service
        CalculateTermsOfServiceStatusResponse calculateTermsOfServiceStatusResponse = getTermsOfServiceStatus(legalEntity.getId());

        // generate Terms of Service
        GetTermsOfServiceDocumentResponse getTermsOfServiceDocumentResponse = getTermsOfServiceDocument(legalEntity.getId());

        // accept Terms of Service
        AcceptTermsOfServiceResponse acceptTermsOfServiceResponse = acceptTermsOfService(legalEntity.getId(), getTermsOfServiceDocumentResponse.getTermsOfServiceDocumentId());

        // retrieve Terms of Service (accepted by the user)
        GetTermsOfServiceAcceptanceInfosResponse getTermsOfServiceAcceptanceInfosResponse = getTermsOfServiceInformationForLegalEntity(legalEntity.getId());

        LOGGER.info("OrganizationOnboarding has completed. Legal Entity ID: {}", legalEntity.getId());

        return legalEntity;

    }

    LegalEntity createLegalEntityCEO() throws IOException, ApiException {

        LegalEntityInfoRequiredType legalEntityInfoRequiredType = new LegalEntityInfoRequiredType()
                .type(LegalEntityInfoRequiredType.TypeEnum.INDIVIDUAL)
                .individual(new Individual()
                        .name(new Name()
                                .firstName("Shelly")
                                .lastName("Eller"))
                        .email("s.eller@example.com")
                        .birthData(new BirthData()
                                .dateOfBirth("1990-06-21"))
                        .phone(new PhoneNumber()
                                .number("+14153671502")
                                .type("mobile"))
                        .residentialAddress(
                                new Address()
                                        .city("New York")
                                        .postalCode("10003")
                                        .stateOrProvince("NY")
                                        .street("71 5th Avenue")
                                        .street2("11th floor")
                                        .country("US")
                        ));

        return legalEntitiesApi.createLegalEntity(legalEntityInfoRequiredType);
    }

    Document createDocumentForLegalEntityCEO(String legalEntityId) throws ApiException, IOException {
        Document document = new Document()
                .description("ID for individual")
                .type(Document.TypeEnum.IDENTITYCARD)
                .addAttachmentsItem(new Attachment()
                        // must be base64-encoded string of supported document type (PDF, JPG, PGN, etc..)
                        .content("JVBERi0xLjQKJcOkw7ezDtsOfCjIgMCBv+f/ub0j6JPRX+E3EmC==".getBytes()))
                .fileName("id-card.pdf")
                .owner(new OwnerEntity()
                        .id(legalEntityId)
                        .type("legalEntity"));

        return documentsApi.uploadDocumentForVerificationChecks(document);
    }

    LegalEntity createLegalEntityCountryManager() throws IOException, ApiException {

        LegalEntityInfoRequiredType legalEntityInfoRequiredType = new LegalEntityInfoRequiredType()
                .type(LegalEntityInfoRequiredType.TypeEnum.INDIVIDUAL)
                .individual(new Individual()
                        .name(new Name()
                                .firstName("Robert")
                                .lastName("Host"))
                        .email("r.host@example.com")
                        .birthData(new BirthData()
                                .dateOfBirth("1990-06-21"))
                        .phone(new PhoneNumber()
                                .number("+14153671502")
                                .type("mobile"))
                        .residentialAddress(
                                new Address()
                                        .city("New York")
                                        .postalCode("10004")
                                        .stateOrProvince("NY")
                                        .street("11 1st Avenue")
                                        .country("US")
                        )
                );

        return legalEntitiesApi.createLegalEntity(legalEntityInfoRequiredType);
    }

    LegalEntity createLegalOrganization() throws IOException, ApiException {

        LegalEntityInfoRequiredType legalEntityInfoRequiredType = new LegalEntityInfoRequiredType()
                .type(LegalEntityInfoRequiredType.TypeEnum.ORGANIZATION)
                .organization(new Organization()
                        .legalName("My Organization Inc.")
                        .registeredAddress(new Address()
                                .city("New York")
                                .postalCode("10005")
                                .stateOrProvince("NY")
                                .street("Times Square")
                                .country("US")
                        )
                );

        return legalEntitiesApi.createLegalEntity(legalEntityInfoRequiredType);
    }

    public LegalEntity associateCEO(String legalEntityId, String legalEntityCEOId) throws IOException, ApiException {

        return legalEntitiesApi.updateLegalEntity(legalEntityId, new LegalEntityInfo()
                        .addEntityAssociationsItem(new LegalEntityAssociation()
                                .jobTitle("CEO")
                                .type(LegalEntityAssociation.TypeEnum.UBOTHROUGHCONTROL)
                                .legalEntityId(legalEntityCEOId))
                );
    }

    public LegalEntity associateCountryManager(String legalEntityId, String legalEntityCountryManagerId) throws IOException, ApiException {

        return legalEntitiesApi.updateLegalEntity(legalEntityId, new LegalEntityInfo()
                .addEntityAssociationsItem(new LegalEntityAssociation()
                        .jobTitle("CEO")
                        .type(LegalEntityAssociation.TypeEnum.SIGNATORY)
                        .legalEntityId(legalEntityCountryManagerId))
        );
    }

    Document createDocumentForLegalEntityCountryManager(String legalEntityId) throws ApiException, IOException {
        Document document = new Document()
                .description("ID for individual")
                .type(Document.TypeEnum.IDENTITYCARD)
                .addAttachmentsItem(new Attachment()
                        // must be base64-encoded string of supported document type (PDF, JPG, PGN, etc..)
                        .content("JVBERi0xLjQKJcOkw7ezDtsOfCjIgMCBv+f/ub0j6JPRX+E3EmC==".getBytes()))
                .fileName("id-card.pdf")
                .owner(new OwnerEntity()
                        .id(legalEntityId)
                        .type("legalEntity"));

        return documentsApi.uploadDocumentForVerificationChecks(document);
    }


    AccountHolder createAccountHolder(String legalEntityId) throws IOException, ApiException {
        AccountHolderInfo accountHolderInfo = new AccountHolderInfo()
                .legalEntityId(legalEntityId)
                .description("Liable account holder")
                .reference("SAMPLE-APP-" + UUID.randomUUID())
                .timeZone("US/Eastern");
        return accountHoldersApi.createAccountHolder(accountHolderInfo);
    }

    TransferInstrument createTransferInstrument(String legalEntityId) throws IOException, ApiException {

        return transferInstrumentsApi.createTransferInstrument(new TransferInstrumentInfo()
                .legalEntityId(legalEntityId)
                .type(TransferInstrumentInfo.TypeEnum.BANKACCOUNT)
                .bankAccount(new BankAccountInfo()
                        .accountIdentification(
                                new BankAccountInfoAccountIdentification(new USLocalAccountIdentification()
                                        .type(USLocalAccountIdentification.TypeEnum.USLOCAL)
                                        .accountType(USLocalAccountIdentification.AccountTypeEnum.CHECKING)
                                        .routingNumber("121202211")
                                        .accountNumber("0000000123")
                                )
                        )
                )
        );
    }

    Document createDocumentForTransferInstrument(String transferInstrumentId) throws ApiException, IOException {
        Document document = new Document()
                .description("Bank statement of TransferInstrument")
                .type(Document.TypeEnum.BANKSTATEMENT)
                .addAttachmentsItem(new Attachment()
                        // must be base64-encoded string of supported document type (PDF, JPG, PGN, etc..)
                        .content("JVBERi0xLjQKJcOkw7ezDtsOfCjIgMCBv+f/ub0j6JPRX+E3EmC==".getBytes()))
                .fileName("bank-statement.pdf")
                .owner(new OwnerEntity()
                        .id(transferInstrumentId)
                        .type("bankAccount"));

        return documentsApi.uploadDocumentForVerificationChecks(document);
    }

    BusinessLine createBusinessLine(String legalEntityId) throws IOException, ApiException {

        return businessLinesApi.createBusinessLine(new BusinessLineInfo()
                .legalEntityId(legalEntityId)
                .service(BusinessLineInfo.ServiceEnum.BANKING)
                .industryCode("4531")
                .addWebDataItem(new WebData()
                        .webAddress("https://www.adyen.com"))
                .sourceOfFunds(new SourceOfFunds()
                        .type(SourceOfFunds.TypeEnum.BUSINESS)
                        .adyenProcessedFunds(false)
                        .description("Funds from my business"))
        );
    }

    CalculateTermsOfServiceStatusResponse getTermsOfServiceStatus(String legalEntityId) throws IOException, ApiException {
        return termsOfServiceApi.getTermsOfServiceStatus(legalEntityId);
    }

    GetTermsOfServiceDocumentResponse getTermsOfServiceDocument(String legalEntityId) throws IOException, ApiException {
        return termsOfServiceApi.getTermsOfServiceDocument(legalEntityId, new GetTermsOfServiceDocumentRequest()
                .type(GetTermsOfServiceDocumentRequest.TypeEnum.ADYENFORPLATFORMSADVANCED)
                .language("en"));
    }

    AcceptTermsOfServiceResponse acceptTermsOfService(String legalEntityId, String termsOfServiceDocumentId) throws IOException, ApiException {
        return termsOfServiceApi.acceptTermsOfService(legalEntityId, termsOfServiceDocumentId, new AcceptTermsOfServiceRequest()
                .acceptedBy(legalEntityId)
                .ipAddress("127.0.0.1"));
    }

    GetTermsOfServiceAcceptanceInfosResponse getTermsOfServiceInformationForLegalEntity(String legalEntityId) throws IOException, ApiException {
        return termsOfServiceApi.getTermsOfServiceInformationForLegalEntity(legalEntityId);
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

    // LEM API key
    private String getLEMApiKey() {
        String key = System.getenv("LEM_API_KEY");

        if(key == null) {
            throw new RuntimeException("LEM_API_KEY is undefined");
        }

        return key;
    }

    // Configuration API key
    private String getBCLApiKey() {
        String key = System.getenv("BCL_API_KEY");

        if(key == null) {
            throw new RuntimeException("BCL_API_KEY is undefined");
        }

        return key;
    }

}
