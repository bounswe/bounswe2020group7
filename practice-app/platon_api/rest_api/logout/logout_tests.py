from django.test import TestCase, Client
from rest_api.models import RegisteredUser

class LogoutTestCase(TestCase):
    def setUp(self):
        RegisteredUser.objects.create(
        								name="Hüsnü", 
        								surname="Şenlendirici",
        								password_hashed="123456", 
        								e_mail="husnu@senlendirici.com",
        								token="husnu_senlendirici_logged_in", 
        								about_me="clarinet",
        								job_uuid="clarinet", 
        								field_of_study="clarinet",
        								forget_password_ans="clarinet"
        							)

    def test_logout_without_token(self):
        """A logout request without token parameter is sent and an error message is sent in return."""
        client = Client()
        response = client.get('/api/logout/')
        self.assertEqual(response.status_code, 400)


    def test_logged_in_user_logout(self):
        """An already logged-in user successfully logs out."""
        test_token = "husnu_senlendirici_logged_in"
        client = Client()
        response = client.get('/api/logout/' + '?' + 'token=' + test_token)
        self.assertEqual(response.status_code, 200)


    def test_non_logged_in_user_logout(self):
        """An user who is not logged in or non-existent tries to log out and receives error message."""
        test_token = "invalid_token"
        client = Client()
        response = client.get('/api/logout/' + '?' + 'token=' + test_token)
        self.assertEqual(response.status_code, 401)

