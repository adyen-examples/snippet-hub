# Using the Adyen Python library

Collection of working code snippets that showcases the usage of the Adyen's API Library for Python ([GitHub](https://github.com/Adyen/adyen-python-api-library)).

**Note:** the folder includes only few snippets as the Adyen Python library sends and receives `JSON` payloads.  

```
    # build payload
    request = {
        "type": "organization",
        "organization": {
            "legalName": "My Organization Inc.",
            "registeredAddress": {
                "city": "New York",
                "country": "US",
                "postalCode": "10005",
                "stateOrProvince": "NY",
                "street": "Times Square 1001",
                "street2": "11th floor"
            }
        }
    }
    # call API
    legal_entity = lem_client.legalEntityManagement.legal_entities_api.create_legal_entity(request)
```
Use the following resources to understand how to construct the request payloads and process the responses:
* [Adyen API Explorer](https://docs.adyen.com/api-explorer/): the comprehensive API portal with the documentation of all Adyen APIs  
* [AdyenDev Postman](https://www.postman.com/adyendev): Postman collections and flows that can be used to explore and execute API calls 

## Learn
Explore the collection of code snippets, understand how the library is setup and used.

## Run
Define your API keys and run the snippets

Define the env variables in the `.env` file:
```
 LEM_API_KEY=your API Key to access LEM API
 BCL_API_KEY=your API Key to access Configuration API
```
Invoke the snippet:
```python
 legal_entity = run_test_organization_onboarding();
```

