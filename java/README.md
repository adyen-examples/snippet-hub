# Using the Adyen Java library

Collection of working code snippets that showcases the usage of the Adyen's API Library for Java ([GitHub](https://github.com/Adyen/adyen-java-api-library)).

**AfP**
* [Individual Onboarding](src/main/java/com/adyen/afp/IndividualOnboarding.java)
* [Organization Onboarding](src/main/java/com/adyen/afp/OrganizationOnboarding.java)
* [Track Transactions](src/main/java/com/adyen/afp/Transactions.java)

## Learn
Explore the collection of code snippets, understand how the library is setup and used.

## Run
Define your API keys and configuration settings to run the snippets

Set env variables:
```shell
    export LEM_API_KEY=your API Key to access LEM API
    export BCL_API_KEY=your API Key to access Configuration API
    export BALANCE_PLATFORM=your Balance Platform
```
Invoke the snippet:
```java
 LegalEntity legalEntity = new OrganizationOnboarding().run();
```

