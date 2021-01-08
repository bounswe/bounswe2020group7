from tests.base_test import BaseTest
from tests.base_test import TestConfig
from app.auth_system.models import User
from app.profile_management.models import *
from app.workspace_system.models import *
from app.auth_system.views import generate_token
from app import db
import datetime
import json
from app.workspace_system.helpers import WorkspaceState
from app.auth_system.models import *
from app.follow_system.models import *

class ActivityStreamTest(BaseTest):
    """
        Unit Tests of the Activity Stream
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
                 "Umut", "Ozdemir", False, None, None, None, 1, "boun"),
            User("can@deneme.com", True, "cce0c2170d1ae52e099c716165d80119ee36840e3252e57f2b2b4d6bb111d8a5", 3.4,
                 "Can", "Bolukbas", False, None, None, None, 2, "boun"),
            User("alperen@deneme.com", True, "hashedpassword", 4.6, "Alperen", "Ozprivate", True, None, None, None, 1, "boun"),
            User("hilal@deneme.com", True, "hasheddpassword", 4.5, "Hilal", "Private", True, None, None, None, 1, "boun")
        ]
        for user in users:
            db.session.add(user)

        db.session.commit()

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

        workspaces = [
            Workspace(creator_id = 1, is_private = 0, title = "coronovirus study", description = "deneme", state = WorkspaceState.search_for_collaborator.value), # public workspace by Umut on state 1
            Workspace(creator_id = 2, is_private = 1, title = "SWE difficulties", description = "deneme", state = WorkspaceState.search_for_collaborator.value), # private workspace by Can on state 1
            Workspace(creator_id = 4, is_private = 1, title = "honeybadger", description = "deneme", state = WorkspaceState.search_for_collaborator.value), # private workspace by Hilal on state 1
            Workspace(creator_id = 3, is_private = 0, title = "bos", description = "deneme", state = WorkspaceState.search_for_collaborator.value) # public workspace by Alperen on state 1
        ]

        for workspace in workspaces:
            db.session.add(workspace)
        
        db.session.commit()

        contributions = [
            Contribution(workspace_id = 1, user_id = 1, is_active = 1), # Umut will be active in coronovirus study
            Contribution(workspace_id = 2, user_id = 2, is_active = 1), # can will be active in SWE difficulties
            Contribution(workspace_id = 2, user_id = 3, is_active = 1),  # alperen will be active in SWE difficulties
            Contribution(workspace_id = 3, user_id = 2, is_active = 0), # can will be inactive in honey badger study
            Contribution(workspace_id = 3, user_id = 4, is_active = 1), # hilal will be active in honey badger study
            Contribution(workspace_id = 3, user_id = 1, is_active = 1), # umut will be active in honey badger study
            Contribution(workspace_id = 4, user_id = 3, is_active = 1) # alperen will be active in bos
        ]

        for contribution in contributions:
            db.session.add(contribution)
        db.session.commit()

        issues = [
            Issue(1, 1, "cure of coronovirus", "eat more sarimsak daily"),
            Issue(2, 2, "alperen's assigned issue", "denemeeee"),
            Issue(4, 3, "bravest animal is honeybadger", "really")
        ]

        for issue in issues:
            db.session.add(issue)
        db.session.commit()

        issue_assignees = [
            IssueAssignee(2, 3), # alperen is assigned on issue which has issue id 2.
            IssueAssignee(1, 1) # umut is assigned on issue wihch has issue id 1.
        ]

        for issue_assignee in issue_assignees:
            db.session.add(issue_assignee)
        db.session.commit()

        issue_comments = [
            IssueComment(2, 2, "makes sense"), # Can makes comment on an issue
            IssueComment(2, 3, "what is this?"), # Alperen makes comment on an issue
            IssueComment(3, 4, "hello world") # Hilal makes comment on an issue
        ]

        for issue_comment in issue_comments:
            db.session.add(issue_comment)
        db.session.commit()

        collab = Collaboration(user_1_id = 1, user_2_id = 2) # umut and can worked together before.
        db.session.add(collab)
        db.session.commit()

    '''
    # Check if the related user started following someone.
    def test_follow_activity(self):
        # Can Follows Umut
        can_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'follower_id': 2, 'following_id': 1}  # 1: Umut, 2: Can
        actual_response = self.client.post('/api/follow/follow_requests', data=data,
                                           headers={'auth_token': can_token})

        # Alperen's activity stream should contain that activity since Alperen follows Can.
        alperen_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': alperen_token})

        expected_output =   {
                                "@context": "https://www.w3.org/ns/activitystreams",
                                "summary": "Page 0 of Activity Stream",
                                "type": "OrderedCollectionPage",
                                "id": 0,
                                "orderedItems": [
                                    {
                                        "@context": {
                                            "@vocab": "https://www.w3.org/ns/activitystreams",
                                            "ext": None,
                                            "@language": "en"
                                        },
                                        "summary": "Can Bolukbas started following Umut Ozdemir",
                                        "type": "Follow",
                                        "actor": {
                                            "type": "Person",
                                            "id": 2,
                                            "name": "Can Bolukbas",
                                            "image": {
                                                "type": "Image",
                                                "url": "/auth_system/logo"
                                            }
                                        },
                                        "object": {
                                            "type": "Person",
                                            "id": 1,
                                            "name": "Umut Ozdemir",
                                            "image": {
                                                "type": "Image",
                                                "url": "/auth_system/logo"
                                            },
                                            "content": None,
                                            "ext:ratingValue": None
                                        },
                                        "target": {
                                                    "type": None,
                                                    "id": None,
                                                    "name": None,
                                                    "image": {
                                                        "type": "Image",
                                                        "url": None
                                                    }
                                                }
                                    }
                                ]
                            }
        
        def ordered_json(cur):
            if isinstance(cur, list):
                return sorted(ordered_json(list_item) for list_item in cur)
            elif isinstance(cur, dict):
                return sorted((key, ordered_json(val)) for key, val in cur.items())
            else:
                return cur

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        ar = ordered_json(actual_response.json)
        eo = ordered_json(expected_output)
        self.assertEqual(ar, eo)


        # Alperen accepts Can's follow request. Can starts following Alperen.
        alperen_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'follower_id': 2, 'following_id': 3, 'state': 1}
        actual_response = self.client.delete('/api/follow/follow_requests', data=data,
                                           headers={'auth_token': alperen_token})

        # Umut's activity stream should contain that activity since Umut follows Can.
        auth_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': auth_token})
        
        expected_output =   {
                                        "@context": "https://www.w3.org/ns/activitystreams",
                                        "summary": "Page 0 of Activity Stream",
                                        "type": "OrderedCollectionPage",
                                        "id": 0,
                                        "orderedItems": [
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": None,
                                                    "@language": "en"
                                                },
                                                "summary": "Can Bolukbas started following Alperen Ozprivate",
                                                "type": "Follow",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 2,
                                                    "name": "Can Bolukbas",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Person",
                                                    "id": 3,
                                                    "name": "Alperen Ozprivate",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    },
                                                    "content": None,
                                                    "ext:ratingValue": None
                                                },
                                                "target": {
                                                            "type": None,
                                                            "id": None,
                                                            "name": None,
                                                            "image": {
                                                                "type": "Image",
                                                                "url": None
                                                            }
                                                        }
                                            },
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": None,
                                                    "@language": "en"
                                                },
                                                "summary": "Can Bolukbas started following Umut Ozdemir",
                                                "type": "Follow",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 2,
                                                    "name": "Can Bolukbas",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Person",
                                                    "id": 1,
                                                    "name": "Umut Ozdemir",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    },
                                                    "content": None,
                                                    "ext:ratingValue": None
                                                },
                                                "target": {
                                                            "type": None,
                                                            "id": None,
                                                            "name": None,
                                                            "image": {
                                                                "type": "Image",
                                                                "url": None
                                                            }
                                                        }
                                            }
                                        ]
                            }
        
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        print(ordered_json(actual_response.json))
        print(ordered_json(expected_output))
        self.assertEqual(ordered_json(actual_response.json), ordered_json(expected_output))

    # Check if related user commented on someone.
    def test_user_comment_activity(self):
        # Umut comments on Can.
        auth_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'commented_user_id': 2, 'rate': 4, 'text': "helal huso"}
        actual_response = self.client.post('/api/follow/comment', data=data,
                                           headers={'auth_token': auth_token})

        # Alperen's activity stream should contain that activity since Alperen follows Umut.
        auth_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': auth_token})

        expected_output =   {
                                "@context": "https://www.w3.org/ns/activitystreams",
                                "summary": "Page 0 of Activity Stream",
                                "type": "OrderedCollectionPage",
                                "id": 0,
                                "orderedItems": [
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": "http://schema.org/Rating",
                                                    "@language": "en"
                                                },
                                                "summary": "Umut Ozdemir commented and rated Can Bolukbas",
                                                "type": "Add",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 1,
                                                    "name": "Umut Ozdemir",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Note",
                                                    "id": 1,
                                                    "name": "Comment",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": None
                                                    },
                                                    "content": "helal huso",
                                                    "ext:ratingValue": 4
                                                },
                                                "target": {
                                                    "type": "Person",
                                                    "id": 2,
                                                    "name": "Can Bolukbas",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                }
                                            }
                                ]
                            }

        def ordered_json(cur):
            if isinstance(cur, list):
                return sorted(ordered_json(list_item) for list_item in cur)
            elif isinstance(cur, dict):
                return sorted((key, ordered_json(val)) for key, val in cur.items())
            else:
                return cur

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        #print(ordered_json(actual_response.json)) 
        #print(ordered_json(expected_output))
        self.assertEqual(ordered_json(actual_response.json), ordered_json(expected_output))

    # Check if related user is now contributing to a workspace. 
    def test_new_contribution(self):
        # Can becomes a new contributor to the workspace(4) titled "bos"

        # Can sends application request.
        auth_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 4}
        actual_response = self.client.post('/api/workspaces/applications', data=data,
                                           headers={'auth_token': auth_token})
        # Alperen accepts application request
        auth_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'application_id': 1, "is_accepted": "1"}
        actual_response = self.client.delete('/api/workspaces/applications', data=data,
                                           headers={'auth_token': auth_token})

        # Umut's activity stream should contain that activity since he follows Can.
        auth_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': auth_token})
        
        expected_output =   {
                                "@context": "https://www.w3.org/ns/activitystreams",
                                "summary": "Page 0 of Activity Stream",
                                "type": "OrderedCollectionPage",
                                "id": 0,
                                "orderedItems": [
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": None,
                                                    "@language": "en"
                                                },
                                                "summary": "Can Bolukbas is now a contributor in bos workspace",
                                                "type": "Join",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 2,
                                                    "name": "Can Bolukbas",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Group",
                                                    "id": 4,
                                                    "name": "bos",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": None
                                                    },
                                                    "content": None,
                                                    "ext:ratingValue": None
                                                },
                                                "target": {
                                                            "type": None,
                                                            "id": None,
                                                            "name": None,
                                                            "image": {
                                                                "type": "Image",
                                                                "url": None
                                                            }
                                                }
                                            }
                                ]
                            }
        def ordered_json(cur):
            if isinstance(cur, list):
                return sorted(ordered_json(list_item) for list_item in cur)
            elif isinstance(cur, dict):
                return sorted((key, ordered_json(val)) for key, val in cur.items())
            else:
                return cur
        
        # print(ordered_json(actual_response.json)) 
        # print(ordered_json(expected_output))
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(ordered_json(actual_response.json), ordered_json(expected_output))


        # Check if this functionality works for invitations also.

        # Alperen sends workspace(4) invitation to Umut(1).
        auth_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'workspace_id': 4, "invitee_id": 1}
        actual_response = self.client.post('/api/workspaces/invitations', data=data,
                                           headers={'auth_token': auth_token})
        # Umut accepts it.
        auth_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'invitation_id': 1, "is_accepted": "1"}
        actual_response = self.client.delete('/api/workspaces/invitations', data=data,
                                           headers={'auth_token': auth_token})

        # Alperen should see this in his activity stream.
        auth_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': auth_token})
        expected_output =   {
                                "@context": "https://www.w3.org/ns/activitystreams",
                                "summary": "Page 0 of Activity Stream",
                                "type": "OrderedCollectionPage",
                                "id": 0,
                                "orderedItems": [
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": None,
                                                    "@language": "en"
                                                },
                                                "summary": "Umut Ozdemir is now a contributor in bos workspace",
                                                "type": "Join",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 1,
                                                    "name": "Umut Ozdemir",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Group",
                                                    "id": 4,
                                                    "name": "bos",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": None
                                                    },
                                                    "content": None,
                                                    "ext:ratingValue": None
                                                },
                                                "target": {
                                                            "type": None,
                                                            "id": None,
                                                            "name": None,
                                                            "image": {
                                                                "type": "Image",
                                                                "url": None
                                                            }
                                                }
                                            },
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": None,
                                                    "@language": "en"
                                                },
                                                "summary": "Can Bolukbas is now a contributor in bos workspace",
                                                "type": "Join",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 2,
                                                    "name": "Can Bolukbas",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Group",
                                                    "id": 4,
                                                    "name": "bos",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": None
                                                    },
                                                    "content": None,
                                                    "ext:ratingValue": None
                                                },
                                                "target": {
                                                            "type": None,
                                                            "id": None,
                                                            "name": None,
                                                            "image": {
                                                                "type": "Image",
                                                                "url": None
                                                            }
                                                }
                                            }
                                ]
                            }
        # print(ordered_json(actual_response.json)) 
        # print(ordered_json(expected_output))
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(ordered_json(actual_response.json), ordered_json(expected_output))

    # Check if related user stopped contributing to a workspace. 
    def test_stop_contribution(self):
        # Can(2) will stop contributing to the workspace(2) titled "SWE difficulties"
        auth_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'workspace_id': 2}
        actual_response = self.client.delete('/api/workspaces/quit', data=data,
                                           headers={'auth_token': auth_token})
        # Since Umut follows Can, he should see this activity in his Activity Stream.
        auth_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': auth_token})
        
        expected_output =   {
                                "@context": "https://www.w3.org/ns/activitystreams",
                                "summary": "Page 0 of Activity Stream",
                                "type": "OrderedCollectionPage",
                                "id": 0,
                                "orderedItems": [
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": None,
                                                    "@language": "en"
                                                },
                                                "summary": "Can Bolukbas left SWE difficulties workspace",
                                                "type": "Leave",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 2,
                                                    "name": "Can Bolukbas",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Group",
                                                    "id": 2,
                                                    "name": "SWE difficulties",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": None
                                                    },
                                                    "content": None,
                                                    "ext:ratingValue": None
                                                },
                                                "target": {
                                                            "type": None,
                                                            "id": None,
                                                            "name": None,
                                                            "image": {
                                                                "type": "Image",
                                                                "url": None
                                                            }
                                                        }
                                                }
                                ]
                            }
        def ordered_json(cur):
            if isinstance(cur, list):
                return sorted(ordered_json(list_item) for list_item in cur)
            elif isinstance(cur, dict):
                return sorted((key, ordered_json(val)) for key, val in cur.items())
            else:
                return cur

        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(ordered_json(actual_response.json), ordered_json(expected_output))
    '''
    # Check if related user created a workspace
    def test_create_workspace(self):
        # Can creates a new public workspace.
        auth_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'title': "Meditator Depression", 'description': "lol"}
        actual_response = self.client.post('/api/workspaces', data=data,
                                           headers={'auth_token': auth_token})
        # Since Umut follows Can, he should see this activity in his Activity Stream.
        auth_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': auth_token})
        
        expected_output =   {
                                "@context": "https://www.w3.org/ns/activitystreams",
                                "summary": "Page 0 of Activity Stream",
                                "type": "OrderedCollectionPage",
                                "id": 0,
                                "orderedItems": [
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": None,
                                                    "@language": "en"
                                                },
                                                "summary": "Can Bolukbas created a workspace named Meditator Depression",
                                                "type": "Create",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 2,
                                                    "name": "Can Bolukbas",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Group",
                                                    "id": 5,
                                                    "name": "Meditator Depression",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": None
                                                    },
                                                    "content": None,
                                                    "ext:ratingValue": None
                                                },
                                                "target": {
                                                            "type": None,
                                                            "id": None,
                                                            "name": None,
                                                            "image": {
                                                                "type": "Image",
                                                                "url": None
                                                            }
                                                        }
                                                }
                                ]
                            }
        def ordered_json(cur):
            if isinstance(cur, list):
                return sorted(ordered_json(list_item) for list_item in cur)
            elif isinstance(cur, dict):
                return sorted((key, ordered_json(val)) for key, val in cur.items())
            else:
                return cur
        
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(ordered_json(actual_response.json), ordered_json(expected_output))

        # If the created workspace is private, don't show it.

        # Can creates a new private workspace.
        auth_token = generate_token(2, datetime.timedelta(minutes=10))
        data = {'title': "Meditator Depression Hurts", 'description': "lol", 'is_private': 1}
        actual_response = self.client.post('/api/workspaces', data=data,
                                           headers={'auth_token': auth_token})
        # Since Umut follows Can, he should see this activity in his Activity Stream.
        auth_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': auth_token})
        
        expected_output =   {
                                "@context": "https://www.w3.org/ns/activitystreams",
                                "summary": "Page 0 of Activity Stream",
                                "type": "OrderedCollectionPage",
                                "id": 0,
                                "orderedItems": [
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": None,
                                                    "@language": "en"
                                                },
                                                "summary": "Can Bolukbas created a workspace named Meditator Depression",
                                                "type": "Create",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 2,
                                                    "name": "Can Bolukbas",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Group",
                                                    "id": 5,
                                                    "name": "Meditator Depression",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": None
                                                    },
                                                    "content": None,
                                                    "ext:ratingValue": None
                                                },
                                                "target": {
                                                            "type": None,
                                                            "id": None,
                                                            "name": None,
                                                            "image": {
                                                                "type": "Image",
                                                                "url": None
                                                            }
                                                        }
                                                }
                                ]
                            }
        # print(ordered_json(actual_response.json))
        # print(ordered_json(expected_output))
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(ordered_json(actual_response.json), ordered_json(expected_output))

    '''
    # Check if related user deleted a workspace
    def test_delete_workspace(self):
        # Umut deletes the workspace(1) titled "Coronovirus Study"
        auth_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1}
        actual_response = self.client.delete('/api/workspaces', data=data,
                                           headers={'auth_token': auth_token})
        # Since Alperen follows Umut, he should see this activity in his Activity Stream.
        auth_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': auth_token})
        
        expected_output =   {
                                "@context": "https://www.w3.org/ns/activitystreams",
                                "summary": "Page 0 of Activity Stream",
                                "type": "OrderedCollectionPage",
                                "id": 0,
                                "orderedItems": [
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "ext": None,
                                                    "@language": "en"
                                                },
                                                "summary": "Umut deleted a workspace",
                                                "type": "Delete",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 1,
                                                    "name": "Umut Ozdemir",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Group",
                                                    "id": 1,
                                                    "name": "coronovirus study",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": None
                                                    },
                                                    "content": None,
                                                    "ext:ratingValue": None
                                                },
                                                "target": {
                                                            "type": None,
                                                            "id": None,
                                                            "name": None,
                                                            "image": {
                                                                "type": "Image",
                                                                "url": None
                                                            }
                                                        }
                                                }
                                ]
                            }
    
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(actual_response.json, expected_output)
    '''
    # This is too complex. For every workspace for every following user we should add this update. This will really slow us down.
    # Check if the workspace of the related user is updated their state.
    '''
    def test_update_state(self):
        # Umut changes the workspace(1) state to ongoing state.
        auth_token = generate_token(1, datetime.timedelta(minutes=10))
        data = {'workspace_id': 1, 'state': WorkspaceState.ongoing.value}
        actual_response = self.client.put('/api/workspaces', data=data,
                                           headers={'auth_token': auth_token})
        # Since Alperen follows Umut, he should see this activity in his Activity Stream.
        auth_token = generate_token(3, datetime.timedelta(minutes=10))
        data = {'page': 0, 'per_page': 10}
        actual_response = self.client.get('/api/activity_stream', query_string=data,
                                           headers={'auth_token': auth_token})
        
        expected_output =   {
                                "@context": "https://www.w3.org/ns/activitystreams",
                                "summary": "Page 0 of Activity Stream",
                                "type": "OrderedCollectionPage",
                                "id": 0,
                                "orderedItems": [
                                            {
                                                "@context": {
                                                    "@vocab": "https://www.w3.org/ns/activitystreams",
                                                    "@language": "en"
                                                },
                                                "summary": "Umut's workspace coronavirus study is updated its state",
                                                "type": "Update",
                                                "actor": {
                                                    "type": "Person",
                                                    "id": 2,
                                                    "name": "Umut Ozdemir",
                                                    "image": {
                                                        "type": "Image",
                                                        "url": "/auth_system/logo"
                                                    }
                                                },
                                                "object": {
                                                    "type": "Group",
                                                    "id": 1,
                                                    "name": "coronovirus study",
                                                }
                                            }
                                ]
                            }
    
        self.assertEqual(actual_response.status_code, 200, 'Incorrect HTTP Response Code')
        self.assertEqual(actual_response.json, expected_output)
    '''
    def tearDown(self):
        super().tearDown()