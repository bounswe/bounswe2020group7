import unittest

#from tests.auth_system_test import LoginTest,ResetPasswordTest
#from tests.follow_test import FollowTest
#from tests.profile_management_test import ResearchInfoTests,NotificationTests
from tests.search_engine_test import SearchHistoryTests, UserSearchTests

from tests.base_test import TestConfig
import sqlalchemy

if __name__ == "__main__":
    engine = sqlalchemy.create_engine("mysql+pymysql://{}:{}@{}:{}".format(TestConfig.mysql_user, TestConfig.mysql_password, TestConfig.mysql_host, TestConfig.mysql_port)) 
    engine.execute("DROP SCHEMA IF EXISTS `platondb_test`;")
    engine.execute("CREATE SCHEMA IF NOT EXISTS `platondb_test`;") 
    unittest.main()
    engine.execute("DROP SCHEMA IF EXISTS platondb_test;")
