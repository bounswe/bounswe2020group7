from django.test import TestCase

from rest_api.delete_user_t.unit_test import DeleteTest
from .register.test import TestRegisterApi
from rest_api.search_engine.test.test_search import SearchTest
from rest_api.update_user.test.test import UpdateTest
from rest_api.news.test.test_news import NewsTestCase

DeleteTest()

UpdateTest()

test_register = TestRegisterApi

SearchTest()

NewsTestCase()