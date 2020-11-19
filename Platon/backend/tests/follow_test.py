from tests.base_test import BaseTest
from tests.base_test import TestConfig
from app.auth_system.models import User
from app.auth_system.views import generate_token
from app.follow_system.models import Follow, FollowRequests
from app import db
import datetime
import json


class FollowTest(BaseTest):
    """
        Unit Tests of the follow module
    """

    def setUp(self):

        db.drop_all()  # precaution.
        db.create_all()

        # Umut and Can are public users. Alperen is private user.
        users = [
            User("umut@deneme.com", True, "b73ec5e4625ffcb6d0d70826f33be7a75d45b37046e26c4b60d9111266d70e32", 3.5,
                 "Umut", "Özdemir", False, None, None, None),
            User("can@deneme.com", False, "cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5", 4.6,
                 "Can", "Deneme", False, None, None, None),
            User("alperen@deneme.com", True, "hashedpassword", 4.6, "Alperen", "Ozprivate", True, None, None, None),
            User("hilal@deneme.com", True, "hasheddpassword", 4.5, "Hilal", "Private", True, None, None, None)
        ]
        for user in users:
            db.session.add(user)

        db.session.commit()

        # Add artificial users to test follow feature.
        follows = [
            Follow(1, 2),  # Umut follows Can
            Follow(3, 2),  # Alperen follows Can
            Follow(3, 1)  # Alperen follows Umut
        ]
        for follow in follows:
            db.session.add(follow)

        db.session.commit()

        follow_requests = [
            FollowRequests(2, 3)  # Can sent follow request to Alperen.
        ]

        for follow_request in follow_requests:
            db.session.add(follow_request)

        db.session.commit()

    # follower: user that follows someone
    # following: user that is followed by someone
    # Possible Improvement: Don't compare lists. Check if the element exists in list.
    def test_get_following_list(self):

        # Returns the following list of the user.
        # e.g. if Umut follows Can, Umut's following list should include Can.

        data = {'follower_id': 1}  # 1: umut
        actual_response = self.client.post('/api/follow/followings', data=data)

        # print(actual_response)
        actual_info = actual_response.data
        expected_info = [2]

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(json.loads(actual_info), expected_info, "Incorrect FollowingID")

    # follower: user that follows someone
    # following: user that is followed by someone
    # Possible Improvement: Don't compare lists. Check if the element exists in list.
    def test_get_follower_list(self):

        # Returns the follower list of the user.
        # e.g. Umut and Alperen follows Can. Then Can's follower list should contain their IDs.

        data = {'following_id': 2}  # 2: can
        actual_response = self.client.post('/api/follow/followers', data=data)

        actual_info = actual_response.data
        expected_info = [1, 3]

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(json.loads(actual_info), expected_info, "Incorrect FollowerIDs")

    # follower: who sends the follow request
    # following: who receives the follow request
    # In the test case, Can sent Alperen a follow request.
    # Possible Improvement: Don't compare lists. Check if the element exists in list.
    def test_get_follow_requests(self):

        valid_token = generate_token(3, datetime.timedelta(minutes=10))  # token created for Alperen.

        data = {'following_id': 3}  # 3: alperen
        actual_response = self.client.post('/api/follow/get_follow_requests', data=data,
                                           headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Check if the follow requests are like expected.
        actual_info = actual_response.data
        expected_info = [2]
        self.assertEqual(json.loads(actual_info), expected_info, "Incorrect FollowerIDs")

        # Token will be created for another user. Login_required should return an error.
        another_token = generate_token(1, datetime.timedelta(minutes=10))  # token created for Umut.
        actual_response = self.client.post('/api/follow/get_follow_requests', data=data,
                                           headers={'auth_token': another_token})
        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

        # Token will not be created. Login_required should return an error.
        actual_response = self.client.post('/api/follow/get_follow_requests', data=data)
        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

    # follower: who sends the follow request
    # following: who receives the follow request
    # In the test case, Umut sends follow request to Hilal. Hilal should see Umut's ID in followRequests list.
    def test_send_follow_requests(self):

        umut_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'follower_id': 1, 'following_id': 4}  # 1: Umut, 4: Hilal
        actual_response = self.client.post('/api/follow/send_follow_requests', data=data,
                                           headers={'auth_token': umut_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Let's see if follow request can be seen by Hilal.
        data = {'following_id': 4}
        hilal_token = generate_token(4, datetime.timedelta(minutes=10))
        follow_requests_list = self.client.post('/api/follow/get_follow_requests', data=data,
                                                headers={'auth_token': hilal_token})
        self.assertEqual(follow_requests_list.status_code, 200, 'Incorrect HTTP Response Code')

        umut_id = 1
        self.assertTrue(umut_id in follow_requests_list.json, "Follow Request of corresponding ID does not exist!")

    # follower: who sends the follow request
    # following: who receives the follow request
    # In the test case, Alperen will accept Can's follow request.
    # Therefore, Alperen's FollowRequests list should not contain Can and his follower list should contain Can.
    def test_accept_follow_request(self):

        alperen_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'follower_id': 2, 'following_id': 3}
        actual_response = self.client.post('/api/follow/accept_follow_requests', data=data,
                                           headers={'auth_token': alperen_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Should return no request found. There is no follow request with these ids.
        data_2 = {'follower_id': 1, 'following_id': 3}
        actual_response = self.client.post('/api/follow/accept_follow_requests', data=data_2,
                                           headers={'auth_token': alperen_token})
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

        # Let Can try to accept the follow request of Alperen. It should return authorization error.
        can_token = generate_token(2, datetime.timedelta(minutes=10))
        actual_response = self.client.post('/api/follow/accept_follow_requests', data=data,
                                           headers={'auth_token': can_token})
        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

    # follower: who sends the follow request
    # following: who receives the follow request
    # In this test case, Alperen will reject Can's follow request.
    def test_reject_follow_request(self):

        alperen_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'follower_id': 2, 'following_id': 3}
        actual_response = self.client.post('/api/follow/reject_follow_requests', data=data,
                                           headers={'auth_token': alperen_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Should return no request found. There is no follow request with these ids.
        data_2 = {'follower_id': 1, 'following_id': 3}
        actual_response = self.client.post('/api/follow/reject_follow_requests', data=data_2,
                                           headers={'auth_token': alperen_token})
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

        # Let Can try to reject the follow request of Alperen. It should return authorization error.
        can_token = generate_token(2, datetime.timedelta(minutes=10))
        actual_response = self.client.post('/api/follow/reject_follow_requests', data=data,
                                           headers={'auth_token': can_token})
        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

    def tearDown(self):
        super().tearDown()
