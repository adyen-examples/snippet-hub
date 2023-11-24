import unittest

from afp.organization_onboarding import main as run_test_organization_onboarding


class MyTestCase(unittest.TestCase):

    def test_organization_onboarding(self):
        legal_entity = run_test_organization_onboarding()
        self.assertIsNotNone(legal_entity)


if __name__ == '__main__':
    unittest.main()
