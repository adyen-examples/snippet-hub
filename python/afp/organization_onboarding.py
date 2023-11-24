import logging
from common.adyen_client import get_lem_client, get_bcl_client


def main():
    logging.basicConfig(format='%(asctime)s - %(levelname)s - %(message)s', level=logging.INFO)
    logging.getLogger('werkzeug').setLevel(logging.ERROR)

    lem_client = get_lem_client()

    # create Legal Entity for the Organization
    request = {
        "type": "organization",
        "organization": {
            "legalName": "My Organization Inc.",
            # "registrationNumber": "1234567890",
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
    legal_entity = lem_client.legalEntityManagement.legal_entities_api.create_legal_entity(request)

    # create Legal Entity CEO
    request = {
        "type": "individual",
        "individual": {
            "residentialAddress": {
                "city": "New York",
                "country": "US",
                "postalCode": "10003",
                "stateOrProvince": "NY",
                "street": "71 5th Avenue",
                "street2": "18th floor"
            },
            "name": {
                "firstName": "Shelly",
                "lastName": "Eller"
            },
            "birthData": {
                "dateOfBirth": "1990-06-21"
            },
            "email": "s.eller@example.com"
        }
    }
    legal_entity_ceo = lem_client.legalEntityManagement.legal_entities_api.create_legal_entity(request)

    return legal_entity
