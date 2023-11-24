import Adyen
import os

from dotenv import load_dotenv, find_dotenv

load_dotenv(find_dotenv())


# create client to access the Legal Entity Management API
def get_lem_client():
    adyen = Adyen.Adyen()
    adyen.client.xapikey = __get_lem_api_key()
    adyen.client.platform = "test"

    return adyen


# create client to access the Configuration API
def get_bcl_client():
    adyen = Adyen.Adyen()
    adyen.client.xapikey = get_bcl_client()
    adyen.client.platform = "test"

    return adyen


def __get_lem_api_key():
    api_key = os.environ.get("LEM_API_KEY")

    if not api_key:
        raise Exception("Missing LEM_API_KEY in .env")

    return api_key


def __get_bcl_api_key():
    api_key = os.environ.get("BCL_API_KEY")

    if not api_key:
        raise Exception("Missing BCL_API_KEY in .env")

    return api_key