from app.workspace_system.models import Collaboration
from tests.base_test import BaseTest
from app.auth_system.models import User
from app.auth_system.views import generate_token
from app.follow_system.models import Follow, FollowRequests, Reports
from app.profile_management.models import Jobs
from app import db
import datetime
import json


class FollowTest(BaseTest):
    """
        Unit Tests of the follow module
    """

    def setUp(self):

        jobs = [
            Jobs("academician"),
            Jobs("PhD student")
        ]

        for job in jobs:
            db.session.add(job)

        db.session.commit()

        # Umut and Can are public users. Alperen is private user.
        users = [
            User("umut@deneme.com", True, "b73ec5e4625ffcb6d0d70826f33be7a75d45b37046e26c4b60d9111266d70e32", 3.5,
                 "Umut", "Özdemir", False, None, None, None, 1, "boun"),
            User("can@deneme.com", True, "cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5", 3.4,
                 "Can", "Deneme", False, None, None, None, 2, "boun"),
            User("alperen@deneme.com", True, "hashedpassword", 4.6, "Alperen", "Ozprivate", True, None, None, None, 1, "boun"),
            User("hilal@deneme.com", True, "hasheddpassword", 4.5, "Hilal", "Private", True, None, None, None, 1, "boun")
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
        valid_token = generate_token(1, datetime.timedelta(minutes=10))
        actual_response = self.client.get('/api/follow/followings', query_string={'follower_id': 1},
                                          headers={'auth_token': valid_token})

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # test the response format also.

    # follower: user that follows someone
    # following: user that is followed by someone
    # Possible Improvement: Don't compare lists. Check if the element exists in list.
    def test_get_follower_list(self):

        # Returns the follower list of the user.
        # e.g. Umut and Alperen follows Can. Then Can's follower list should contain their IDs.
        valid_token = generate_token(1, datetime.timedelta(minutes=10))
        actual_response = self.client.get('/api/follow/followers', query_string={'following_id': 2},
                                          headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # test the response format also.

    # follower: who sends the follow request
    # following: who receives the follow request
    # In the test case, Can sent Alperen a follow request.
    # Possible Improvement: Don't compare lists. Check if the element exists in list.
    def test_get_follow_requests(self):

        valid_token = generate_token(3, datetime.timedelta(minutes=10))  # token created for Alperen.

        actual_response = self.client.get('/api/follow/follow_requests', query_string={'following_id': 3},
                                           headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Check if the follow request exists.
        fr_dict = actual_response.json
        mylist = [str(fr['id']) for fr in fr_dict["follow_requests"]]
        self.assertTrue('2' in mylist, "Follow Requests does not contain Can's follow request.")

        # Token will be created for another user. Should return an error.
        another_token = generate_token(1, datetime.timedelta(minutes=10))  # token created for Umut.
        actual_response = self.client.get('/api/follow/follow_requests', query_string={'following_id': 3},
                                           headers={'auth_token': another_token})
        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

        # Token will not be created. Login_required should return an error.
        actual_response = self.client.get('/api/follow/follow_requests', query_string={'following_id': 3})
        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')
        
    # follower: who sends the follow request
    # following: who receives the follow request
    # In the test case, Umut sends follow request to Hilal. Hilal should see Umut's ID in followRequests list.
    # Possible Improvement: Check if the same follow request already exists in database.
    def test_send_follow_requests(self):

        umut_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'follower_id': 1, 'following_id': 4}  # 1: Umut, 4: Hilal
        actual_response = self.client.post('/api/follow/follow_requests', data=data,
                                           headers={'auth_token': umut_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Let's see if follow request can be seen by Hilal.
        data = {'following_id': 4}
        hilal_token = generate_token(4, datetime.timedelta(minutes=10))
        follow_requests_list = self.client.get('/api/follow/follow_requests', query_string=data,
                                                headers={'auth_token': hilal_token})
        self.assertEqual(follow_requests_list.status_code, 200, 'Incorrect HTTP Response Code')

        umut_id = 1
        mylist = [fr['id'] for fr in follow_requests_list.json['follow_requests']]
        self.assertTrue(umut_id in mylist, "Follow Request of corresponding ID does not exist!")

        # Another Case. Let's follow a public profile. It should create Follow record, not FollowRequest record.
        can_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'follower_id': 2, 'following_id': 1}  # 1: Umut, 2: Can
        actual_response = self.client.post('/api/follow/follow_requests', data=data,
                                           headers={'auth_token': can_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        follow_list = self.client.get('/api/follow/followers',
                                      query_string={'following_id': 1},
                                      headers={'auth_token': can_token})
        self.assertEqual(follow_list.status_code, 200, 'Incorrect HTTP Response Code')
        fr_dict = follow_list.json
        mylist = [fr['id'] for fr in fr_dict['followers']]
        self.assertTrue(2 in mylist, "Follow record of corresponding ID does not exist!")

    # follower: who sends the follow request
    # following: who receives the follow request
    # In the test case, Alperen will accept Can's follow request.
    # Therefore, Alperen's FollowRequests list should not contain Can and his follower list should contain Can.
    def test_accept_follow_request(self):

        alperen_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'follower_id': 2, 'following_id': 3, 'state': 1}
        actual_response = self.client.delete('/api/follow/follow_requests', data=data,
                                           headers={'auth_token': alperen_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Should return no request found. There is no follow request with these ids.
        data_2 = {'follower_id': 1, 'following_id': 3, 'state': 1}
        actual_response = self.client.delete('/api/follow/follow_requests', data=data_2,
                                           headers={'auth_token': alperen_token})
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

        # Let Can try to accept the follow request of Alperen. It should return authorization error.
        can_token = generate_token(2, datetime.timedelta(minutes=10))
        actual_response = self.client.delete('/api/follow/follow_requests', data=data,
                                           headers={'auth_token': can_token})
        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

    # follower: who sends the follow request
    # following: who receives the follow request
    # In this test case, Alperen will reject Can's follow request.
    def test_reject_follow_request(self):

        alperen_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'follower_id': 2, 'following_id': 3, 'state': 2}
        actual_response = self.client.delete('/api/follow/follow_requests', data=data,
                                           headers={'auth_token': alperen_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # Should return no request found. There is no follow request with these ids.
        data_2 = {'follower_id': 1, 'following_id': 3, 'state': 2}
        actual_response = self.client.delete('/api/follow/follow_requests', data=data_2,
                                           headers={'auth_token': alperen_token}, )
        self.assertEqual(actual_response.status_code, 404, 'Incorrect HTTP Response Code')

        # Let Can try to reject the follow request of Alperen. It should return authorization error.
        can_token = generate_token(2, datetime.timedelta(minutes=10))
        actual_response = self.client.delete('/api/follow/follow_requests', data=data,
                                           headers={'auth_token': can_token})
        self.assertEqual(actual_response.status_code, 401, 'Incorrect HTTP Response Code')

    def test_unfollow(self):
        valid_token = generate_token(1, datetime.timedelta(minutes=10))  # token created for Umut.
        data = {'following_id': 2}  # 2: can
        actual_response = self.client.delete('/api/follow/followings', data=data, headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

        # check if Umut's followings list contains Can.
        data = {'follower_id': 1}
        rp = self.client.get('/api/follow/followings', query_string=data, headers={'auth_token': valid_token})
        self.assertEqual(rp.status_code, 200, 'Incorrect HTTP Response Code')
        fl_dict = rp.json
        mylist = [fl['id'] for fl in fl_dict['followings']]
        self.assertTrue(2 not in mylist, 'Unfollow is not successful')

    def tearDown(self):
        super().tearDown()

class ReportTest(BaseTest):
    """
        Unit Tests of the report module
    """
    def setUp(self):

        jobs = [
            Jobs("academician"),
            Jobs("PhD student")
        ]

        for job in jobs:
            db.session.add(job)

        db.session.commit()

        # Umut and Can are public users. Alperen is private user.
        users = [
            User("umut@deneme.com", True, "b73ec5e4625ffcb6d0d70826f33be7a75d45b37046e26c4b60d9111266d70e32", 3.5,
                 "Umut", "Özdemir", False, None, None, None, 1, "boun"),
            User("can@deneme.com", True, "cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5", 3.4,
                 "Can", "Deneme", False, None, None, None, 2, "boun"),
            User("alperen@deneme.com", True, "hashedpassword", 4.6, "Alperen", "Ozprivate", True, None, None, None, 1, "boun"),
            User("hilal@deneme.com", True, "hasheddpassword", 4.5, "Hilal", "Private", True, None, None, None, 1, "boun")
        ]
        for user in users:
            db.session.add(user)

        db.session.commit()

        db.session.commit()

        collaborations = [
            Collaboration(user_1_id=1, user_2_id=2),
            Collaboration(user_1_id=2, user_2_id=1)
        ]

        for collaboration in collaborations:
            db.session.add(collaboration)
        db.session.commit()

        # Add artificial users to test follow feature.
        follows = [
            Follow(1, 2),  # Umut follows Can
            Follow(3, 2),  # Alperen follows Can
            Follow(3, 1)  # Alperen follows Umut
        ]
        for follow in follows:
            db.session.add(follow)

        reports = [
            Reports(1,3,"dsds")
        ]

        for report in reports:
            db.session.add(report)
        db.session.commit()


    def test_post_report_valid(self):
        valid_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {
            'reported_user_id': 2,
            'text': "report"
        }
        expected_response = {
            'msg': 'Report addition is successfully sent via email'
        }
        actual_response = self.client.post('/api/follow/report',data=data,headers={'auth_token': valid_token})
        self.assertEqual(expected_response, json.loads(actual_response.data))
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

    def test_post_report_invalid(self):
        valid_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {
            'reported_user_id': 3,
            'text': "report"
        }
        expected_response = {
            'error': 'You can not send comments to the user which you did not worked before'
        }
        actual_response = self.client.post('/api/follow/report',data=data,headers={'auth_token': valid_token})
        self.assertEqual(expected_response, json.loads(actual_response.data))
        self.assertEqual(actual_response.status_code, 403, 'Incorrect HTTP Response Code')

    def test_get_reports(self):
        valid_token = generate_token(1, datetime.timedelta(minutes=10))
        actual_response = self.client.get('/api/follow/report', headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')

    def test_xdelete_report(self):
        valid_token = generate_token(1, datetime.timedelta(minutes=10))
        actual_response = self.client.delete('/api/follow/report',query_string={'report_id': 1},headers={'auth_token': valid_token})
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')


    def tearDown(self):
        super().tearDown()