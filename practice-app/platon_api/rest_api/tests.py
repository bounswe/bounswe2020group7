from django.test import TestCase
from rest_api.delete_user_t.unit_test import DeleteTest
from .register.test import TestRegisterApi
from rest_api.search_engine.test.test_search import SearchTest

DeleteTest()

test_register = TestRegisterApi

SearchTest()

