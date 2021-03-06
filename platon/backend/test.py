import unittest

from tests.milestone_test import MilestoneTest
from tests.issues_test import IssuesTest
from tests.auth_system_test import LoginTest,ResetPasswordTest
from tests.follow_test import FollowTest, ReportTest
from tests.profile_management_test import ResearchInfoTests,NotificationTests
from tests.search_engine_test import SearchHistoryTests, WorkspaceSearchTests, UserSearchTests, UpcomingEventsSearchTest
from tests.upcoming_events_test import UpcomingEventsTest
from tests.recommendation_system_test import RecommendationSystemTest
from tests.activity_stream_test import ActivityStreamTest

from tests.base_test import TestConfig
import sqlalchemy

if __name__ == "__main__":
    engine = sqlalchemy.create_engine("mysql+pymysql://{}:{}@{}:{}".format(TestConfig.mysql_user, TestConfig.mysql_password, TestConfig.mysql_host, TestConfig.mysql_port)) 
    engine.execute("DROP SCHEMA IF EXISTS `platondb_test`;")
    engine.execute("CREATE SCHEMA IF NOT EXISTS `platondb_test`;") 
    unittest.main()
    engine.execute("DROP SCHEMA IF EXISTS platondb_test;")
