from django.test import TestCase

# Create your tests here.

from webapp.models import Company


class CompanyModelTest(TestCase):
    """
        Test if print version of name returns expected result
    """
    def test_print_name(self):
        name = "Aldi"
        company = Company(name=name)
        self.assertIs(company.__str__(), name)