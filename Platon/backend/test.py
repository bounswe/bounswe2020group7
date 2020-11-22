import unittest
from tests.auth_system_test import LoginTest,ResetPasswordTest
#from tests.follow_test import FollowTest
from tests.profile_management_test import ResearchInfoTests,NotificationTests
import sqlalchemy

if __name__ == "__main__":
    engine = sqlalchemy.create_engine("mysql+pymysql://root:rootpassword@52.59.254.130:3306") 
    engine.execute("DROP SCHEMA IF EXISTS `platondb_test`;")
    engine.execute("CREATE SCHEMA IF NOT EXISTS `platondb_test`;") 
    unittest.main()
    engine.execute("DROP SCHEMA IF EXISTS platondb_test;")
